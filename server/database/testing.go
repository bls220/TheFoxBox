package database

import (
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"log"
	"../dt"
)

func Test() {
	DestroyDB()
	CreateUserTable()
	CreateSongTable()
	CreateUserSongTable()
	db := GetDB()
	
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

	rows, err := db.Query("select id, name from song")
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
