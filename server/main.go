
package main

import "./gop3"
import "fmt"
import "./dt"

import (
	"os"
	"./database"
)

const PREFIX = "/mnt/usb/music/"

func initFirstTime() {
	if _, err := os.Stat(database.DB_PATH); err != nil {
		fmt.Println("Indexing songs for the first time...")
		if err = Scan(PREFIX); err != nil {
			fmt.Println("Error scanning library:", err)
			os.Exit(1)
		}
		fmt.Println("Indexing finished!")
	}
}

func toPath(s dt.Song) string {
	fmt.Println("-----------------------------------------")
	fmt.Println(s)
	fmt.Println(s.Artist)
	fmt.Println(s.Album)
	fmt.Println(s.Title)
	fmt.Println("-----------------------------------------")
	
	return PREFIX + s.Artist + "/" + s.Album + "/" + s.Title
}

func main() {
	fmt.Println("STARTED")
	initFirstTime()
	go runServer(":5853", GotConn)
	s := gop3.InitMp3()
	fmt.Println("Initialization complete! Welcome to SkyNet.")
	
	for x := range theDJ.GetPlaylist() {
		theDJ.NowPlaying(x)
		s.PlaySong(toPath(x))
		s.WaitForSong()
		theDJ.SongOver()
	}
}
