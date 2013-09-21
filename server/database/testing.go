package database

import (
	"fmt"
	"../dt"
)

func Test() {
	DestroyDB()
	CreateUserTable()
	CreateSongTable()
	CreateVoteTable()
	songs := make([]dt.Song,0,10)
	songs = append(songs,dt.Song{Title: "What the frog says"});
	songs = append(songs,dt.Song{Title: "What the ben says"});
	fmt.Println(songs)
	AddSongs(songs)
	//GetSongs()
	fmt.Println(GetSongsByChaos(3))
}
