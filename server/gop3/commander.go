package gop3

import (
	"fmt"
)

type B struct{}

type actType uint
type reqType uint
const (
	PLAY actType = iota
)
func (a actType) String() string {
	switch a {
		case PLAY:
			return "Play"
		default:
			return "Unknown action type!"
	}
}
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
	
	go ret.initThenLoop()
	
	return ret
}

func (m*Mp3Commander) loop() {
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
	fmt.Println("Got action:", act.ty, act.params)
	switch act.ty {
		case PLAY:
			return m.playSong(act.params[0])
	}
	panic("Unknown action!")
}

func (m*Mp3Commander) doReq(req mp3Request) error {
	panic("Unimplemented!")
}

func (m*Mp3Commander) PlaySong(path string) {
	m.acts <- mp3Action{ PLAY, []string{path,} }
}

// Only one goroutine may wait on songs at a time
func (m*Mp3Commander) WaitForSong() {
	<-m.songWait
}
