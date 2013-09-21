package main

import (
	"./dt"
	"sync"
	"container/heap"
)

// Ideas:
//		no less than 5 songs in the queue at once.
//		When less songs are in the queue, the program guesses what songs could be played until there are 5 in the queue
//			Songs added in this way will have their 'points' set according to how likely it thinks people will like the
//				song.

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
	m sync.Mutex
	songs SongQueue
	
	// Contains the song ID if the song was 'recently' played
	recent map [int]B
	recentlyPlayed [100]int
	recentlyPlayedIndex int
}

func (s*DJ) Vote(songid, points int) string {
	s.m.Lock()
	defer s.m.Unlock()
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
	s.m.Lock()
	defer s.m.Unlock()
	
	if _, ok := s.recent[songid]; ok {
		return "Sorry, this song has been played too recently!"
	}
	

	// TODO: DB: Get song info here (caches the song value so that we don't have to do a db hit
	//    every time we want to do something.
	song := SongPoint{
		dt.Song{songid, "Dummy Artist", "Dummy album", "Dummy title"},
		points,
	}
	
	s.recent[songid] = B{}
	heap.Push(&s.songs, song)
	
	prev := s.recentlyPlayed[s.recentlyPlayedIndex]
	s.recentlyPlayed[s.recentlyPlayedIndex] = songid
	s.recentlyPlayedIndex = (s.recentlyPlayedIndex + 1) % len(s.recentlyPlayed)
	
	// Remove the song from recent memory so that it can be played again
	delete(s.recent, prev)
	
	return "" // Blank for no error
}

