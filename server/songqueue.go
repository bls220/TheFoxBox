package main

import (
	"./dt"
	"sync"
	"container/heap"
	"sort"
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
func (s*SongQueue) Len() int {
	return len(s.songs)
}
func (s*SongQueue) Less(i, j int) bool {
	return s.songs[i].points > s.songs[j].points
}
func (s*SongQueue) Swap(i, j int) {
	s.songs[i], s.songs[j] = s.songs[j], s.songs[i]
}
func (s*SongQueue) Push(x interface{}) {
	s.songs = append(s.songs, x.(SongPoint))
}
func (s*SongQueue) Pop() interface{} {
	ll := len(s.songs)-1
	var ret interface{}
	ret, s.songs = s.songs[ll-1], s.songs[:ll-1]
	return ret
}

//Singleton
var theDJ = DJ{}
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
	
	cpy := make([]SongPoint, len(s.songs.songs))
	copy(cpy, s.songs.songs)
	ss := SongQueue{cpy}
	sort.Sort(&ss)
	
	ll := len(ss.songs)
	if s.isPlaying {
		ll++
	}
	ret := make([]dt.Song, ll)
	if s.isPlaying {
		ret[0] = s.nowPlaying
	}
	for i, v := range ss.songs {
		ret[i+1] = v.s
	}
	
	return ret, nil
}

func (s*DJ) prime() error {
	s.Lock()
	needSongs := 6 - s.songs.Len()
	s.Unlock() // Don't lock during this (db could block)

	if needSongs > 0 {
		newSongs, err := SuggestSongs(needSongs)
		if err == nil {
			s.Lock()
			for _,x := range newSongs {
				heap.Push(&s.songs, SongPoint{x, 0})
			}
			s.Unlock()
		} else if s.songs.Len() == 0 {
			return err //Ignore the error until we're out of songs
		}
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
	return heap.Pop(&s.songs).(SongPoint).s, nil
}

func (s*DJ) Vote(songid, points int) string {
	s.Lock()
	defer s.Unlock()
	indx := -1
	// Linear search through this short list won't be too bad
	for i,x := range s.songs.songs {
		if x.s.Id == songid {
			indx = i
			break
		}
	}
	if indx == -1 {
		return "Could not find song in queue to vote on!"
	}
	
	song := s.songs.songs[indx]
	heap.Remove(&s.songs, indx)
	song.points += points
	heap.Push(&s.songs, song)
	return ""
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
	
	songPoint := SongPoint{
		song,
		points,
	}
	
	s.recent[songid] = B{}
	heap.Push(&s.songs, songPoint)
	
	prev := s.recentlyPlayed[s.recentlyPlayedIndex]
	s.recentlyPlayed[s.recentlyPlayedIndex] = songid
	s.recentlyPlayedIndex = (s.recentlyPlayedIndex + 1) % len(s.recentlyPlayed)
	
	// Remove the song from recent memory so that it can be played again
	
	// (TODO: prevent ids from ever being 0 so we can just check for that rather than
	//    going though the map check)
	if _, ok := s.recent[prev]; ok {
		delete(s.recent, prev)
	}
	
	return "" // Blank for no error
}

