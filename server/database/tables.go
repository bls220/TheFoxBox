package database

import (
	"database/sql"
	_ "github.com/mattn/go-sqlite3"
	"log"
	"os"
)

const DB_PATH string = "./thedb.db";

func DestroyDB(){
	os.Remove(DB_PATH)
}

func CreateUserTable(){
	createTable(
		`create table user (
			id 		integer not null primary key, 
			name	text, 
			admin	integer
			);
		delete from user;`)
}

func CreateSongTable(){
	createTable(
		`create table song (
			id 		integer not null primary key, 
			name 	text
			);
		delete from song;`)
}

func CreateMoodTable(){
	createTable(
		`create table mood (
			id		integer not null primary key, 
			song 	references song(id),
			user 	references user(id),
			like	integer,
			r 		integer,
			g 		integer,
			b  		integer
			);
		delete from song;`)
}


func createTable(table string){
	db, err := sql.Open("sqlite3", DB_PATH)
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()
	_, err = db.Exec(table)
	if err != nil {
		log.Printf("%q: %s\n", err, table)
		return
	}
}
