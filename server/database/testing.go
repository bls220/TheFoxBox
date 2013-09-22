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
	AddSongs([]dt.Song{dt.Song{Title: "What the frog says"}, 
				dt.Song{Title: "What the fox says"},
				dt.Song{Title: "What the dog says"},
				dt.Song{Title: "What the cat says"}})
	//GetSongs()
	fmt.Println(GetSongsByString("fox"))

	AddUser(dt.User{Name: "Travis",Admin: true})
	AddVote(dt.Vote{Song: dt.Song{Id: 1}, User: dt.User{Id: 1}, Like: true, Mood: dt.Mood{10,10,10}})
	AddVote(dt.Vote{Song: dt.Song{Id: 2}, User: dt.User{Id: 1}, Like: true, Mood: dt.Mood{50,50,50}})
	fmt.Println(GetSongsByMood(dt.Mood{50,50,50}))
}
