
package main

import "./gop3"
import "fmt"

func main() {
	fmt.Println("Started")

	s := gop3.InitMp3()
	s.PlaySong("capella.mp3")
	select{}
}
