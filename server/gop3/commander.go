package gop3

// #cgo LDFLAGS: -lmpg123 -lao
/*
#include <mpg123.h>
#include <ao/ao.h>
*/
import "C"

import (
	"runtime"
	"fmt"
)

// A type used to request data/actions from the mp3 thread
type Mp3Commander struct {
	acts chan mp3Action
	reqs chan mp3Request
	kill chan B
	
	songWait chan B
	
	handle *C.mpg123_handle
	driver C.int
}

type B struct{}

type actType uint
type reqType uint
const (
	PLAY actType = iota
)
const (
	ID3_REQ reqType = iota
)

type mp3Action struct {
	ty actType
	params []string
}

type mp3Request struct {
	ty reqType
	retChan chan<-[]string
	params []string
}

func InitMp3() *Mp3Commander {
	ret := &Mp3Commander {
		acts: make(chan mp3Action), //TODO: buffed?
		reqs: make(chan mp3Request, 5),
		kill: make(chan B),
		songWait: make(chan B),
	}
	
	go mp3Loop(ret)
	
	return ret
}

//Finishes initialization of the Mp3Commander
func mp3Loop(m*Mp3Commander) {
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
	
	for {
		var err error
		select {
			case act := <-m.acts:
				err = m.doAct(act)
			case req := <-m.reqs:
				err = m.doReq(req)
			case <-m.kill:
				fmt.Println("Killing mp3 thread")
				return
		}
		if err != nil {
			// TODO: better
			fmt.Println("ERR: ", err)
		}
	}
}

// Allows requests to be served in between long-running actions
func (m*Mp3Commander) schedule() error {
	select {
		case r := <-m.reqs:
			return m.doReq(r)
		default:
			return nil
	}
}

func (m*Mp3Commander) doAct(act mp3Action) error {
	fmt.Println("Got action:", act)
	switch act.ty {
		case PLAY:
			return m.playSong(act.params[0])
	}
	panic("Unknown action!")
}

func (m*Mp3Commander) doReq(req mp3Request) error {
	panic("Unimplemented!")
}


