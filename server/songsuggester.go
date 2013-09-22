
package main

import (
	"math/rand"
	"./klog"
	"./dt"
	"errors"
	"./database"
)


var Suggestor = klog.Module("Song suggestor")

func SuggestSongs(count int) (ret []dt.Song, err error) {
	r := rand.New(rand.NewSource(rand.Int63()))
	_=r
	tries := 0
	for count > 0 {
		var thisTry []dt.Song
		/*
		switch r.Intn(4) {
			case 0:
				thisTry, err = database.GetSongsByRoom(loggedInUsers, count)
			case 1:
				thisTry, err = database.GetSongsByMood(loggedInUsers.AverageMood(), count)
			case 2:
				thisTry, err = database.GetSongsByChaos(count)
			case 3:
				thisTry, err = database.GetBestFavs(count)
		}*/
		thisTry, err = database.GetSongsByChaos(count)
		
		if err != nil {
			klog.Warning(Suggestor, "choosing a song", err)
		} else if ll := len(thisTry); ll > 0 {
			if ll < count {
				count -= ll
			} else {
				thisTry = thisTry[:count]
				count = 0
			}
			ret = append(ret, thisTry...)
		}
		
		tries++
		if tries > 10 {
			// Want to stop it from spinning forever in worst case!
			return nil, errors.New("Could not find enough songs to suggest after a bunch of tries!")
		}
	}
	
	return ret, nil
}
