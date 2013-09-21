
package main

import "./gop3"
import "fmt"

func main() {
	fmt.Println("Started")

	s := gop3.InitMp3()
	s <- "capella.mp3"
	close(s)
	select{}
}
