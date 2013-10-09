package gop3

import (
	"fmt"
	"time"
)

// A type used to request data/actions from the mp3 thread
type Mp3Commander struct {
	acts chan mp3Action
	reqs chan mp3Request
	kill chan B
	
	songWait chan B
}


func (m*Mp3Commander) initThenLoop() {
	m.loop()
}


func (m*Mp3Commander) playSong(path string) error {
	fmt.Println("Pretending to play song", path)
	
	time.Sleep(5 * time.Second)
	
	select {
		case m.songWait<-B{}:
		default:
	}
	
	return nil
}

