
package gop3


// #cgo LDFLAGS: -lmpg123 -lao
/*
#include <mpg123.h>
#include <ao/ao.h>
*/
import "C"

import (
	"fmt"
	"unsafe"
	"errors"
)

func (m*Mp3Commander) PlaySong(path string) {
	m.acts <- mp3Action{ PLAY, []string{path,} }
}

func (m*Mp3Commander) playSong(path string) error {
	f := C.CString(path)
	defer C.free(unsafe.Pointer(f))
	var channels, encoding C.int
	var rate C.long
	var format C.ao_sample_format

	if err := C.mpg123_open(m.handle, f); err != C.MPG123_OK {
		return errors.New(fmt.Sprint("Could not open ", path, ":", err))
	}
	defer C.mpg123_close(m.handle)

	C.mpg123_getformat(m.handle, &rate, &channels, &encoding)
	format.bits = 16
	format.channels = channels
	format.rate = C.int(rate)
	format.byte_format = C.AO_FMT_LITTLE

	dev := C.ao_open_live(m.driver, &format, nil)
	if dev == nil {
		panic("Cannot open device!")
	}
	defer C.ao_close(dev)

	var actual C.size_t
	var buffer [1024*4]C.uchar
	ubuf := (*C.uchar)(unsafe.Pointer(&buffer[0]))
	buf  := (*C. char)(unsafe.Pointer(&buffer[0]))

	fmt.Println("Playing song:", path)
	llen := C.size_t(len(buffer))
	for {
		if ok := C.mpg123_read(m.handle, ubuf, llen, &actual); ok == C.MPG123_DONE {
			break
		}
		act := C.uint_32(actual)
		
		C.ao_play(dev, buf, act)
		m.schedule()
	}
	
	select {
		case m.songWait<-B{}:
		default:
	}
	
	return nil
}

func (m*Mp3Commander) WaitForSong() {
	<-m.songWait
}