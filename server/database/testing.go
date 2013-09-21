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
	AddSongs(dt.Song{Title: "What the frog says"}, 
				dt.Song{Title: "What the fox says"},
				dt.Song{Title: "What the dog says"},
				dt.Song{Title: "What the cat says"})
	//GetSongs()
	fmt.Println(GetSongsByChaos(3))
}
