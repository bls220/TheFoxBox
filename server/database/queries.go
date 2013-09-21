package database

import (
	_ "github.com/mattn/go-sqlite3"
	"../dt"
	"database/sql"
	"fmt"
)

func GetUser(name string) {
	
}
func AddUser(user dt.User){
	
}

func AddSongs(songs []dt.Song) error {
	f := func(db *sql.DB) error {
		for _, x := range songs {
			if _, err := db.Exec(fmt.Sprintf("INSERT into song(title,album,artist) values('%s','%s','%s')",x.Title,x.Album,x.Artist)); err != nil {
				return err
			}
		}
		return nil
	}
	return doTransaction(f)
}

func AddVote(vote dt.Vote) error {
	f := func(db *sql.DB) error {
		_, err := db.Exec(fmt.Sprintf("INSERT into vote(song,user,like,r,g,b) values('%d','%d','%d','%d','%d','%d')",vote.Song.Id,vote.User.Id,vote.Like,vote.Mood.R,vote.Mood.G,vote.Mood.B));
		return err
	}
	return doTransaction(f)
}
func GetSongsByString(search string) ([]dt.Song, error) {
	str := "%"+search + "%"
	return getSongsGeneric(fmt.Sprintf("SELECT * FROM song WHERE title LIKE '%s'", str))
}

func GetSongsByRoom(room dt.Room){

} 

func GetSongs() ([]dt.Song, error) {
	return getSongsGeneric("SELECT id, title FROM song")
}

func GetSongsByChaos(num int) ([]dt.Song, error) {
	return getSongsGeneric(fmt.Sprintf("SELECT * FROM song ORDER BY RANDOM() limit %d", num))
}


func getSongsGeneric(query string) ([]dt.Song, error){

	songs := []dt.Song{}
	f := func(db *sql.DB) error {
		rows, err := db.Query(query)
		if err != nil {
			return err
		}
		defer rows.Close()
		songs = convSongs(rows)
		return nil
	}
	if err := doTransaction(f); err != nil {
		return nil, err
	}
	return songs, nil
}

func convSongs(rows *sql.Rows) []dt.Song {
	songs := []dt.Song{}
	for rows.Next() {
		song := dt.Song{}
		rows.Scan(&song.Id,&song.Title,&song.Album,&song.Artist,&song.Genre)
		songs = append(songs,song)
	}
	return songs
}

type DBCallback func(*sql.DB) error
func doTransaction(call DBCallback) error {
	db, err := sql.Open("sqlite3", DB_PATH)
	if err != nil {
		return err
	}
	defer db.Close()
	tx, err := db.Begin();
	if err != nil {
		return err
	}
	
	if err := call(db); err != nil {
		tx.Rollback()
		return err
	} else {
		tx.Commit()
		return nil
	}
}


