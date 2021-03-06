
package main

import (
	"./dt"
	"./database"
	"path/filepath"
	"strings"
	"os"
)

type slHolder struct {
	sl []dt.Song
}

func Scan(dir string) error {
	database.DestroyDB()
	database.CreateUserTable()
	database.CreateSongTable()
	database.CreateVoteTable()

	sl := slHolder{ make([]dt.Song, 0, 100) }
	if err := filepath.Walk(dir, makeWalkFunc(dir, &sl)); err != nil {
		return err
	}
	return database.AddSongs(sl.sl)
}

func makeWalkFunc(prefix string, sl*slHolder) filepath.WalkFunc {
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
		
		path = path[len(prefix):]
		
		// Platform independence
		path = strings.Replace(path, "\\", "/", -1)
		
		spl := strings.Split(path, "/")
		s := dt.Song{Artist: spl[0], Album: spl[1], Title: spl[2], }
		sl.sl = append(sl.sl, s)
		
		return nil
	}
}








