
// +build !mock

package gop3

//Finishes initialization of the Mp3Commander
func (m*Mp3Commander) initThenLoop() {
	runtime.LockOSThread()
	if err := C.mpg123_init(); err != C.MPG123_OK {
		panic(fmt.Sprint("Err initing mpg123:", err))
	}
	defer C.mpg123_exit()

	h := C.mpg123_new(nil, nil)
	if h == nil {
		panic(fmt.Sprint("Could not create new mpg123"))
	}
	defer C.mpg123_delete(h)
	m.handle = h

	//TODO: Err check
	C.ao_initialize()
	defer C.ao_shutdown()

	m.driver = C.ao_default_driver_id()
	
	m.loop()
}

// A type used to request data/actions from the mp3 thread
type Mp3Commander struct {
	acts chan mp3Action
	reqs chan mp3Request
	kill chan B
	
	songWait chan B
	
	handle *C.mpg123_handle
	driver C.int
}