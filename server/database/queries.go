package database

import (
	_ "github.com/mattn/go-sqlite3"
	"../dt"
	"log"
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
			if _, err := db.Exec(fmt.Sprintf("insert into song(title) values('%s')",x.Title)); err != nil {
				return err
			}
		}
		return nil
	}
	return doTransaction(f)
}

func AddVote(vote dt.Vote) error {
	f := func(db *sql.DB) error {
		_, err := db.Exec(fmt.Sprintf("insert into vote(song,user,like,r,g,b) values('%s')",vote.Song.Id,vote.User.Id,vote.Like,vote.Mood.R,vote.Mood.G,vote.Mood.B));
		return err
	}
	return doTransaction(f)
}
func GetSongByName(name string){

}

func GetSongByMoodAndRoom(mood dt.Mood, room dt.Room){

} 

func GetSongs() ([]dt.Song, error) {
	songs := []dt.Song{}
	f := func(db *sql.DB) error {
		rows, err := db.Query("select id, title from song")
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
		rows.Scan(&song.Id,&song.Title)
		songs = append(songs,song)
	}
	return songs
}

func GetSongsByChaos(num int) ([]dt.Song, error) {
	songs := []dt.Song{}
	f := func(db *sql.DB) error {
		rows, err := db.Query(fmt.Sprintf("select * from song order by RANDOM() limit %d", num))
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
	return song, nil
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


