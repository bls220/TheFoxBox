package main

import (
	"./dt"
	"sync"
	"fmt"
	"./database"
)

//TODO: AI: When a song is suggested, give some weight to the decision based on how much it thinks the song will be liked.

type B struct{}

type SongPoint struct {
	s dt.Song
	points int
}
type SongQueue struct {
	songs []SongPoint
}
func (s SongQueue) Len() int {
	return len(s.songs)
}
func (s*SongQueue) Insert(so dt.Song, points int) {
	spot := -1
	for i, v := range s.songs {
		if v.points < points {
			spot = i
			break
		}
	}
	sp := SongPoint{so, points}
	if spot == -1 {
		// Didn't find, just append it and finish
		s.songs = append(s.songs, sp)
	} else {
		// Insert into the list
		llen := len(s.songs) - 1
		// Make a spot for it
		s.songs = append(s.songs, SongPoint{})
		copy(s.songs[spot:llen],s.songs[spot+1:llen+1])
		s.songs[spot] = sp
	}
}

func (s*SongQueue) Dequeue() dt.Song {
	ret := s.songs[0].s
	s.songs = s.songs[1:]
	return ret
}

// Returns true on error
// TODO: Factor out repeated code in here and Insert
func (s*SongQueue) Modify(id, points int) bool {
	sl := s.songs
	indx := -1
	// Linear search through this short list won't be too bad
	for i,x := range sl {
		if x.s.Id == id {
			indx = i
			break
		}
	}
	if indx == -1 {
		return true
	}
	
	pp := &sl[indx].points
	newPoints := *pp + points
	*pp = newPoints
	
	// Bubble up the value up to its sorted spot
	last := &sl[indx]
	for i := indx - 1; i >= 0; i-- {
		cur := &sl[i]
		
		if cur.points > last.points {
			// Stop when the current position has more points
			break
		}
		
		*last, *cur = *cur, *last
		last = cur
	}
	
	return false
}

//Singleton
var theDJ = DJ{recent:make(map[int]B),}
type DJ struct {
	// Must be used to guard all methods
	sync.Mutex
	songs SongQueue
	
	nowPlaying dt.Song
	isPlaying bool
	
	// Contains the song ID if the song was 'recently' played
	recent map [int]B
	recentlyPlayed [100]int
	recentlyPlayedIndex int
}

func (s*DJ) GetQueue() ([]dt.Song, error) {
	if err := s.prime(); err != nil {
		return nil, err
	}
	
	s.Lock()
	defer s.Unlock()
	
	baseList := s.songs.songs
	
	ll := len(baseList)
	if s.isPlaying {
		ll++
	}
	ret := make([]dt.Song, ll)
	var workingSet []dt.Song
	if s.isPlaying {
		ret[0] = s.nowPlaying
		workingSet = ret[1:]
	} else {
		workingSet = ret[:]
	}
	for i, v := range baseList {
		workingSet[i] = v.s
	}
	
	return ret, nil
}

func (s*DJ) prime() error {
	s.Lock()
	needSongs := 6 - s.songs.Len()
	s.Unlock() // Don't lock during this (db could block)

	for needSongs > 0 {
		newSongs, err := SuggestSongsForRoom(needSongs)
		if err != nil {
			if s.songs.Len() == 0 {
				return err
			}
			return nil // Ignore the error while we still have stuff left
		}
		s.Lock()
		for _,x := range newSongs {
			if !s.addSong(x, 0, true) {
				needSongs--
			}
		}
		s.Unlock()
	}
	
	return nil
}

// This is a terrible way to implement this!
func (s*DJ) GetPlaylist() <-chan dt.Song {
	ret := make(chan dt.Song)
	
	go s.playlistLoop(ret)
	
	return ret
}

func (s*DJ) playlistLoop(out chan<-dt.Song) {
	for {
		song, err := s.GetNextSong()
		if err != nil {
			fmt.Println("Error trying to get next song:", err)
			close(out)
			return
		}
		out <- song
	}
}

func (s*DJ) NowPlaying(x dt.Song) {
	s.Lock()
	s.isPlaying = true
	s.nowPlaying = x
	s.Unlock()
}

func (s*DJ) SongOver() {
	s.Lock()
	s.isPlaying = false
	s.Unlock()
}

func (s*DJ) GetNextSong() (dt.Song, error) {
	if err := s.prime(); err != nil {
		return dt.Song{}, err
	}
	s.Lock()
	defer s.Unlock()
	return s.songs.Dequeue(), nil
}

func (s*DJ) Vote(songid, points int) string {
	s.Lock()
	defer s.Unlock()
	
	if s.songs.Modify(songid, points) {
		return "Could not find song to vote on!"
	}
	return ""
}

// The lock NEEDS to be held before calling this method!
// Returns true on error
func (s*DJ) addSong(song dt.Song, points int, checkMap bool) bool {
	songid := song.Id
	if checkMap {
		if _, ok := s.recent[songid]; ok {
			return true
		}
	}
	
	s.recent[songid] = B{}
	s.songs.Insert(song, points)
	
	prev := s.recentlyPlayed[s.recentlyPlayedIndex]
	s.recentlyPlayed[s.recentlyPlayedIndex] = songid
	s.recentlyPlayedIndex = (s.recentlyPlayedIndex + 1) % len(s.recentlyPlayed)
	
	// Remove the song from recent memory so that it can be played again
	
	// (TODO: prevent ids from ever being 0 so we can just check for that rather than
	//    going though the map check)
	if _, ok := s.recent[prev]; ok {
		delete(s.recent, prev)
	}
	return false
}

func (s*DJ) AddSong(songid, points int) string {
	s.Lock()
	defer s.Unlock()
	
	if _, ok := s.recent[songid]; ok {
		return "Sorry, this song has been played too recently!"
	}
	
	song, err := database.GetSong(songid)
	if err != nil {
		return err.Error()
	}
	
	if s.addSong(song, points, false) {
		return "There was some error adding the song to the queue!"
	}
	return "" // Blank for no error
}

