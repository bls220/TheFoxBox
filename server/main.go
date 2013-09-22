
package main

import _ "./gop3"
import "fmt"

import (
	"os"
	"./database"
)

func initFirstTime() {
	if _, err := os.Stat(database.DB_PATH); err != nil {
		fmt.Println("Indexing songs for the first time...")
		if err = Scan("/mnt/usb/music"); err != nil {
			panic(err)
		}
		fmt.Println("Indexing finished!")
	}
}

func main() {
	fmt.Println("STARTED")
	initFirstTime()
	runServer(":5853", GotConn)
	//s := gop3.InitMp3()
	//fmt.Println("Initialization complete! Welcome to SkyNet.")
	
	//s.PlaySong("capella.mp3")
	
	
	
	
	select{}
}
