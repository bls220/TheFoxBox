
package main

import (
	"./dt"
	//"./database"
	"path/filepath"
	"strings"
	"os"
)

type slHolder struct {
	sl []dt.Song
}

func Scan(dir string) error {
	sl := slHolder{ make([]dt.Song, 0, 100) }
	if err := filepath.Walk(dir, makeWalkFunc(dir, &sl)); err != nil {
		return err
	}
	return nil//database.AddSongs(sl.sl)
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
		
		path = path[len(prefix)+1:]
		
		// Platform independence
		path = strings.Replace(path, "\\", "/", -1)
		
		spl := strings.Split(path, "/")
		sl.sl = append(sl.sl, dt.Song{Artist: spl[0], Album: spl[1], Title: spl[2], })
		
		return nil
	}
}








