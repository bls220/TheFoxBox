package database

import (
	"../dt"
	"fmt"
	"math/rand"
)



const DB_PATH = "./thedb.db"

func RecreateDB() error { return nil }

func AddUser(user dt.User) error { return nil}
func AddSongs(songs []dt.Song) error { return nil}
func AddVote(vote dt.Vote) error { return nil}

func GetSongsByString(search string) ([]dt.Song, error) {
	return getRandomSonglist(10, search), nil
}
func GetSongsByMood(mood dt.Mood, count int) ([]dt.Song, error) {
	return getRandomSonglist(count, "Mood Song"), nil
}
func GetSongsByChaos(count int) ([]dt.Song, error) {
	return getRandomSonglist(count, "Chaos Song"), nil
}
func GetSongsByRoom(room dt.Room, count int) ([]dt.Song, error) {
	return getRandomSonglist(count, "Room Song"), nil
}
func GetBestFavs(count int) ([]dt.Song, error) {
	return getRandomSonglist(count, "Favs Song"), nil
}

func GetSong(id int) (dt.Song, error) {
	return getMockSong(id, "Artist"), nil
}

func getMockSong(id int, artist string) dt.Song {
	return dt.Song { id, artist, "Album", fmt.Sprint("Song #", id), "Genre" }
}

func getRandomSonglist(count int, artist string) []dt.Song {
	ret := make([]dt.Song, count)
	for i := range ret {
		ret[i] = getMockSong(int(rand.Int31n(400)), artist)
	}
	return ret
}