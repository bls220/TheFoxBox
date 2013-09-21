
package main

import (
	//"errors"
	
	"path/filepath"
	"strings"
	"os"
)

import "fmt"

func Scan(dir string) {
	err := filepath.Walk(dir, makeWalkFunc(dir))
	if err != nil {
		fmt.Println("ERR WALKING:", err)
	}
}

func makeWalkFunc(prefix string) filepath.WalkFunc {
	return func(path string, info os.FileInfo, err error) error {
		if info.IsDir() {
			return nil
		}
		if err != nil {
			return err
		}
		
		if !strings.HasSuffix(path, ".mp3") {
			return nil
		}
		
		path = path[len(prefix)+1:]
		
		// Platform independence
		path = strings.Replace(path, "\\", "/", -1)
		
		spl := strings.Split(path, "/")
		fmt.Println(strings.Join(spl, "||"))
		
		return nil
	}
}








