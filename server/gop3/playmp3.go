
package gop3


// #cgo LDFLAGS: -lmpg123 -lao
/*
#include <mpg123.h>
#include <ao/ao.h>
*/
import "C"

import (
	"fmt"
	"runtime"
	"unsafe"
	"errors"
)


func InitMp3() chan<-string {
	ret := make(chan string)

	go mp3Loop(ret)

	return ret
}


func p(s string) {
	fmt.Println(s)
}


func playSong(path string, m *C.mpg123_handle, driver C.int) error {
	f := C.CString(path)
	defer C.free(unsafe.Pointer(f))
	var channels, encoding C.int
	var rate C.long
	var format C.ao_sample_format

	if err := C.mpg123_open(m, f); err != C.MPG123_OK {
		return errors.New(fmt.Sprint("Could not open ", path, ":", err))
	}
	defer C.mpg123_close(m)

	C.mpg123_getformat(m, &rate, &channels, &encoding)
	format.bits = 16
	format.channels = channels
	format.rate = C.int(rate)
	format.byte_format = C.AO_FMT_LITTLE

	dev := C.ao_open_live(driver, &format, nil)
	if dev == nil {
		panic("Cannot open driver!")
	}
	defer C.ao_close(dev)

	var actual C.size_t
	var buffer [1024*4]C.uchar
	ubuf := (*C.uchar)(unsafe.Pointer(&buffer[0]))
	buf  := (*C. char)(unsafe.Pointer(&buffer[0]))

	fmt.Println(path)
	llen := C.size_t(len(buffer))
	for {
		if ok := C.mpg123_read(m, ubuf, llen, &actual); ok == C.MPG123_DONE {
			break
		}
		act := C.uint_32(actual)
		C.ao_play(dev, buf, act)
	}
	return nil
}

func mp3Loop(in <-chan string) {
	runtime.LockOSThread()
	if err := C.mpg123_init(); err != C.MPG123_OK {
		panic(fmt.Sprint("Err initing mpg123:", err))
	}
	defer C.mpg123_exit()

	m := C.mpg123_new(nil, nil)
	if m == nil {
		panic(fmt.Sprint("Could not create new mpg123"))
	}
	defer C.mpg123_delete(m)

	//TODO: Err check
	C.ao_initialize()
	defer C.ao_shutdown()

	driver := C.ao_default_driver_id()

	for song := range in {
		err := playSong(song, m, driver)
		if err != nil {
			fmt.Println("ERR playing song:", err)
		}
	}
}
