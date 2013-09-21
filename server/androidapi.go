package main

import (
	"net"
	"encoding/json"
	
	"fmt"
)


type Song struct {
	Artist, Album, Title string
	Votes int
}

func songlist(conn net.Conn) error {
	dummyData := []Song {
		Song{ "Kevin and the Malachowskis", "Bob", "Kevin's Song", 5},
		Song{ "Bob and the Joes", "Kevin", "Bob's Song", 4},
		Song{ "Jesse and the girls", "Untitled", "Jesse's girl", -2},
	}
	
	if str, err := json.Marshal(dummyData); err != nil {
		return err
	} else {
		fmt.Println(string(str))
		llen := len(str)
		
		a,b,c,d := byte((llen >>  0) & 0xFF),
		           byte((llen >>  8) & 0xFF),
		           byte((llen >> 16) & 0xFF),
		           byte((llen >> 24) & 0xFF)
		conn.Write([]byte{a,b,c,d})
		conn.Write((str))
	}
	
	return nil
}