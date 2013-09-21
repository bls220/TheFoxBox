package database

import (
	_ "github.com/mattn/go-sqlite3"
	"../dt"
	"log"
	"fmt"
)

func GetUser(name string) {
	
}
func AddUser(){

}

func AddSong(song dt.Song){
	dbExec(fmt.Sprintf("insert into song(name) values(%s)",song.Title))
}
func GetSongByName(name string){

}

func GetSongByMoodAndRoom(mood dt.Mood, room dt.Room){

} 

func GetSongByChaos(num int){

}

func dbExec(qry string){
	db := GetOpenDB()
	_, err := db.Exec(qry)
	if err != nil {
		log.Fatal(err)
	}
}