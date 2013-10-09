
package main

import (
	"math/rand"
	"./klog"
	"./dt"
	"errors"
	"./database"
)


var Suggestor = klog.Module("Song suggestor")

type SuggestHeuristic func(int) ([]dt.Song, error)

func SuggestSongsForPerson(count int, u *dt.User) (ret []dt.Song, err error) {
	return SuggestSongs(count,
		func(nc int) ([]dt.Song, error) {
			return database.GetSongsByMood(u.CurMood, nc)
		}, func(nc int) ([]dt.Song, error) {
			return database.GetSongsByChaos(nc)
		},
	)
}

func SuggestSongsForRoom(count int) (ret []dt.Song, err error) {
	return SuggestSongs(count,
		func(nc int) ([]dt.Song, error) {
			return database.GetSongsByRoom(loggedInUsers, nc)
		}, func(nc int) ([]dt.Song, error) {
			return database.GetSongsByMood(loggedInUsers.AverageMood(), nc)
		}, func(nc int) ([]dt.Song, error) {
			return database.GetSongsByChaos(nc)
		}, func(nc int) ([]dt.Song, error) {
			return database.GetBestFavs(nc)
		},
	)
}

func SuggestSongs(count int, heuristics...SuggestHeuristic) (ret []dt.Song, err error) {
	r := rand.New(rand.NewSource(rand.Int63()))
	_=r
	tries := 0
	l := len(heuristics)
	for count > 0 {
		var thisTry []dt.Song
		thisTry, err = heuristics[r.Intn(l)](count)
		
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



