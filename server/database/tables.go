// +build !mock

package database

import (
	"database/sql"
	_ "github.com/mattn/go-sqlite3"
	"os"
)

const DB_PATH string = "./thedb.db";

func RecreateDB() error {
	if e := destroyDB(); e != nil { return e }
	if e := createUserTable(); e != nil { return e }
	if e := createSongTable(); e != nil { return e }
	if e := createVoteTable(); e != nil { return e }
	return nil
}

func destroyDB() error {
	return os.Remove(DB_PATH)
}

func createUserTable() error {
	return createTable(
		`create table user (
			id 		integer not null primary key, 
			name	text, 
			admin	integer 
			);
		delete from user;`)
}

func createSongTable() error {
	return createTable(
		`create table song (
			id 		integer not null primary key, 
			title 	text,
			album	text,
			artist	text,
			genre 	text
			);
		delete from song;`)
}

func createVoteTable() error {
	return createTable(
		`create table vote (
			id		integer not null primary key, 
			song 	references song(id),
			user 	references user(id),
			like	integer,
			r 		integer,
			g 		integer,
			b  		integer
			);
		delete from vote;`)
}


func createTable(table string) error {
	db, err := sql.Open("sqlite3", DB_PATH)
	if err != nil {
		return err
	}
	_, err = db.Exec(table)
	db.Close()
	return err
}
