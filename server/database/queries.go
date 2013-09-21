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

func AddSong(song dt.Song){
	f := func(db *sql.DB) bool {
		_, err := db.Exec(fmt.Sprintf("insert into song(title) values('%s')",song.Title));
		return err != nil 
	}
	doTransaction(f)
}

func AddVote(user dt.User, mood dt.Mood, song dt.Song){

	f := func(db *sql.DB) bool {
		_, err := db.Exec(fmt.Sprintf("insert into mood(song) values('%s')",song.Title));
		return err != nil
	}
	doTransaction(f)
}
func GetSongByName(name string){

}

func GetSongByMoodAndRoom(mood dt.Mood, room dt.Room){

} 

func GetSongs() []dt.Song{
	songs := []dt.Song{}
	f := func(db *sql.DB) bool {
		rows, err := db.Query("select id, title from song")
		defer rows.Close()
		songs = convSongs(rows)
		return err != nil
	}
	doTransaction(f)
	return songs
}
func convSongs(rows *sql.Rows) []dt.Song{
	songs := []dt.Song{}
	for rows.Next() {
		song := dt.Song{}
		rows.Scan(&song.Id,&song.Title)
		songs = append(songs,song)
	}
	return songs
}

func GetSongsByChaos(num int) []dt.Song{
	songs := []dt.Song{}
	f := func(db *sql.DB) bool {
		rows, err := db.Query(fmt.Sprintf("select * from song order by RANDOM() limit %d",num))
		defer rows.Close()
		songs = convSongs(rows)
		return err != nil
	}
	doTransaction(f)
	return songs
}

// Returns true on error (transaction should be rolled back)
type DBCallback func(*sql.DB) bool

func doTransaction(call DBCallback) {
	db, err := sql.Open("sqlite3", DB_PATH)
	if err != nil {
		log.Fatal(err)
	}
	tx, err := db.Begin();
	if err != nil {
		log.Fatal(err)
	}
	
	if call(db) {
		tx.Rollback()
		log.Fatal(err)
	} else {
		tx.Commit()
	}

	db.Close()
}


