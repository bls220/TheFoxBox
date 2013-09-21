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

func AddVote(user dt.User, mood dt.Mood, Song dt.Song){

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

func GetSongs(){

	f := func(db *sql.DB) bool {
		rows, err := db.Query("select id, title from song")
		defer rows.Close()
		for rows.Next() {
			var id int
			var title string
			rows.Scan(&id, &title)
			fmt.Println(id, title)
		}
		return err != nil
	}
	doTransaction(f)
}

func GetSongByChaos(num int){
	f := func(db *sql.DB) bool {
		/*stmt := db.Prepare()
		stmt.Exec()



		if for some reason we fail {
			return true
		}
		return false*/
		return false
	}

	doTransaction(f)
}


/*func dbExec(db, qry string){
	_, err := db.Exec(qry)
	if err != nil {
		log.Fatal(err)
	}
}*/

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


