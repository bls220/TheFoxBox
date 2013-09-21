package main

import (
	"net"
	"encoding/json"
	"io"
	"fmt"
	
	"./dt"
	"./klog"
	
	"sync"
	"errors"
	"strings"
	"strconv"
)

var AAM = klog.Module("Android API")


type AndroidRequest struct {
	AuthToken string
	Request string
	Params map[string]string
	
	conn net.Conn
}

func (m*AndroidRequest) Respond(d interface{}) error {
	if str, err := json.Marshal(d); err != nil {
		return err
	} else {
		llen := len(str)
		
		a,b,c,d := byte((llen >>  0) & 0xFF),
		           byte((llen >>  8) & 0xFF),
		           byte((llen >> 16) & 0xFF),
		           byte((llen >> 24) & 0xFF)
		m.conn.Write([]byte{a,b,c,d})
		m.conn.Write((str))
		return nil
	}
}

// Checks that all of the keys were provided in the json request. Will return true iff there
//  were keys not found.
func (req*AndroidRequest) require(keys...string) bool {
	p := req.Params
	for _, x := range keys {
		if _, ok := p[x]; !ok {
			return true
		}
	}
	return false
}

func procSongList(req string, conn*AndroidRequest) error {
	dummyData := struct {
		Request string
		Songs []dt.Song
	} {
		req,
		[]dt.Song{
			dt.Song{ 0, "Kevin and the Malachowskis", "Bob", "Kevin's Song"},
			dt.Song{ 1, "Bob and the Joes", "Kevin", "Bob's Song"},
			dt.Song{ 2, "Jesse and the girls", "Untitled", "Jesse's girl"},
		},
	}
	
	return conn.Respond(dummyData)
}

func GotConn(conn net.Conn) error {
	var lens [4]byte
	if n, err := conn.Read(lens[:]); err != nil {
		return err
	} else if n != 4 {
		return errors.New("Too little data read")
	}
	
	llen := ((lens[0]&0xFF) <<  0) |
	        ((lens[1]&0xFF) <<  8) |
	        ((lens[2]&0xFF) << 16) |
	        ((lens[3]&0xFF) << 24)
	
	buf := make([]byte, llen)
	if _, err := io.ReadFull(conn, buf); err != nil {
		return errors.New("Unable to fully read the payload from the client")
	}
	
	req := &AndroidRequest{}
	if err := json.Unmarshal(buf, req); err != nil {
		return err
	}
	req.conn = conn
	
	klog.Info(AAM, "Got request:", req.Request)
	switch req.Request {
		case "vote":
			return procVote(req)
		case "submit":
			return procSubmit(req)
		case "moodchange":
			return procMoodChange(req)
		case "songlist":
			return procSongList("songlist", req)
		case "search":
			return procSearch(req)
		case "login":
			return logInUser(req)
	}
	return errors.New(fmt.Sprint("Unknown request:", req))
}


/*
   This map is shared among multiple goroutines and therefore must be guarded by a mutex.
	It maps an AuthToken to the corresponding User
*/
var loggedInUsers map[string]*dt.User
var loggedInUsersMutex sync.Mutex
// Also guarded by loggedInUsersMutex
var nextAuthToke uint64

func (req*AndroidRequest) getUser() *dt.User {
	loggedInUsersMutex.Lock()
	defer loggedInUsersMutex.Unlock()
	if u, ok := loggedInUsers[req.AuthToken]; ok {
		return u
	}
	return nil
}
// Returns the AuthToken for the user
func logUserIn(u*dt.User) string {
	loggedInUsersMutex.Lock()
	toke := nextAuthToke
	nextAuthToke++
	auth := fmt.Sprintf("%08X", toke)
	loggedInUsers[auth] = u
	loggedInUsersMutex.Unlock()
	
	return auth
}

var TokenNotFound = errors.New("The AuthToken provided was invalid")
var KeysNotFound = errors.New("Could not find all required keys for the request")
var InvalidFormat = errors.New("The request had a bad format for at least one of its parameters")

// Implementations of the api:

func logInUser(req*AndroidRequest) error {
	if req.require("Name") { return KeysNotFound }
	
	//TODO: DB: Hook into the DB here to get the User
	u := &dt.User{Name: req.Params["Name"],}
	ret := struct {
		Request string
		AuthToken string
	} {
		"login",
		logUserIn(u),
	}
	return req.Respond(ret)
}

func procMoodChange(req*AndroidRequest) error {
	if req.require("Mood") { return KeysNotFound }
	u := req.getUser()
	if u == nil { return TokenNotFound }
	
	m := strings.Split(req.Params["Mood"], ";")
	if len(m) != 3 {
		return InvalidFormat
	}
	
	if r, err := strconv.Atoi(m[0]); err != nil {
		return InvalidFormat
	} else if g, err := strconv.Atoi(m[1]); err != nil {
		return InvalidFormat
	} else if b, err := strconv.Atoi(m[2]); err != nil {
		return InvalidFormat
	} else {
		u.CurMood = dt.Mood{r,g,b}
	}
	
	return nil
}

func procSearch(req*AndroidRequest) error {
	if req.require("Term") { return KeysNotFound }
	u := req.getUser()
	if u == nil { return TokenNotFound }

	//TODO: DB: search for this
	return procSongList("search", req)
}

func procVote(req*AndroidRequest) error {
	if req.require("Id", "Amt") { return KeysNotFound }
	u := req.getUser()
	if u == nil { return TokenNotFound }
	
	//TODO: DB: Add this vote to the DB
	if id, err := strconv.Atoi(req.Params["Id"]); err != nil {
		return err
	} else if votes, err := strconv.Atoi(req.Params["Amt"]); err != nil {
		return err
	} else {
		theDJ.Vote(id, votes)
	}
	return nil
}

func procSubmit(req*AndroidRequest) error {
	if req.require("Id") { return KeysNotFound }
	u := req.getUser()
	if u == nil { return TokenNotFound }
	
	//TODO: DB: Add this submission as a pseudo-vote to the DB
	if id, err := strconv.Atoi(req.Params["Id"]); err != nil {
		return err
	} else {
		// TODO: AI: Calculate predicted initial points of the song.
		err := theDJ.AddSong(id, 0)
		ret := struct {
			Request string
			Ret string
		} {
			"submit",
			err,
		}
		return req.Respond(ret)
	}
}







