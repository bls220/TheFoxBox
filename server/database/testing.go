package database

import (
	"database/sql"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"log"
	"os"
)

/*
	create table user (id integer not null primary key, name text, admin integer);
	create table userSong (id integer not null primarykey,
							foreign key(song) references song(id)
							);

create table song (id integer not null primary key, name text);
	create table user (id integer not null primary key, name text, admin integer);
	delete from song;
	delete from user;
*/
func Test() {
	os.Remove("./foo.db")

	db, err := sql.Open("sqlite3", "./foo.db")
	if err != nil {
		log.Fatal(err)
	}
	defer db.Close()

	sql := `create table user (
		id 		integer not null primary key, 
		name	text, 
		admin	integer
		);
		delete from user;`
	_, err = db.Exec(sql)
	if err != nil {
		log.Printf("%q: %s\n", err, sql)
		return
	}

	sql = `create table song (
		id 		integer not null primary key, 
		name 	text
		);
		delete from song;`
	_, err = db.Exec(sql)
	if err != nil {
		log.Printf("%q: %s\n", err, sql)
		return
	}

	sql = `create table user_song (
			id		integer not null primary key, 
			song 	references song(id),
			user 	references user(id),
			like	integer,
			colour	text
			);
		delete from song;`
	_, err = db.Exec(sql)
	if err != nil {
		log.Printf("%q: %s\n", err, sql)
		return
	}


	tx, err := db.Begin()
	if err != nil {
		log.Fatal(err)
	}
	
	stmt, err := tx.Prepare("insert into song(name) values(?)")
	if err != nil {
		log.Fatal(err)
	}
	defer stmt.Close()
	for i := 0; i < 10; i++ {
		_, err = stmt.Exec("What Does The Fox Say")
		if err != nil {
			log.Fatal(err)
		}
	}
	tx.Commit()

	_, err = db.Exec("insert into user(name, admin) values('derp',1), ('herp',0)")
	if err != nil {
		log.Fatal(err)
	}

	_, err = db.Exec("insert into user_song(song,user) values(1,1)")
	if err != nil {
		log.Fatal(err)
	}

	rows, err := db.Query("select id, name from user")
	if err != nil {
		log.Fatal(err)
	}
	defer rows.Close()
	for rows.Next() {
		var id int
		var name string
		rows.Scan(&id, &name)
		fmt.Println(id, name)
	}
}
