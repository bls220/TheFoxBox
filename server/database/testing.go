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
	AddSong(dt.Song{Title: "What the frog says"})
	AddSong(dt.Song{Title: "What the fox says"})
	AddSong(dt.Song{Title: "What the dog says"})
	AddSong(dt.Song{Title: "What the cat says"})
	//GetSongs()
	fmt.Println(GetSongsByChaos(3))
}
