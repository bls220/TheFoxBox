package database

import (
	_ "github.com/mattn/go-sqlite3"
	"../dt"
	"database/sql"
	"fmt"
	"strconv"
)

const MOOD_RANGE int = 10;

func GetUser(name string) {
	
}
func AddUser(user dt.User) error{
	f := func(db *sql.DB) error {
		_, err := db.Exec(fmt.Sprintf("INSERT into user(name,admin) values('%s','%d')",user.Name,user.Admin));
		return err
	}
	return doTransaction(f)
}

func AddSongs(songs []dt.Song) error {
	f := func(db *sql.DB) error {
		for _, x := range songs {
			if _, err := db.Exec(fmt.Sprintf("INSERT into song(title,album,artist) values('%s','%s','%s')",x.Title,x.Album,x.Artist)); err != nil {
				return err
			}
		}
		return nil
	}
	return doTransaction(f)
}

func AddVote(vote dt.Vote) error {
	f := func(db *sql.DB) error {
		_, err := db.Exec(fmt.Sprintf("INSERT into vote(song,user,like,r,g,b) values('%d','%d','%d','%d','%d','%d')",vote.SongId,vote.UserId,vote.Like,vote.Mood.R,vote.Mood.G,vote.Mood.B));
		return err
	}
	return doTransaction(f)
}

func GetSongsByString(search string) ([]dt.Song, error) {
	str := "%"+search+"%"
	return getSongsGeneric(fmt.Sprintf("SELECT * FROM song WHERE title LIKE '%s' LIMIT 100", str))
}


func GetSongsByMood(mood dt.Mood) ([]dt.Song, error) {
	return getSongsGeneric(fmt.Sprintf("SELECT * FROM vote WHERE like=\"1\" AND r BETWEEN %d AND %d AND g BETWEEN %d AND %d AND b BETWEEN %d AND %d",
										mood.R-MOOD_RANGE,mood.R+MOOD_RANGE,
										mood.G-MOOD_RANGE,mood.G+MOOD_RANGE,
										mood.B-MOOD_RANGE,mood.B+MOOD_RANGE))
}

func GetSongsByRoom(room dt.Room) ([]dt.Song, error) {
	avgMood := room.AverageMood()
	ids := room.GetUserIdsInRoom()
	
	if len(ids) == 0 {
		return []dt.Song{}, nil
	}
	
	idStr := `"` + strconv.Itoa(ids[0]) + `"`
	for _, id := range ids[1:] {
		idStr += `,"` + strconv.Itoa(id) + `"`
	}
	
	sql := `select avg(r) as avgr, avg(g) as avgg, avg(b) as avgb, song
				from vote where user IN (` + idStr + `) AND like="1" AND GROUP BY song HAVING ` +
				fmt.Sprintf("avgr BETWEEN %d AND %d AND avgg BETWEEN %d AND %d AND avgb BETWEEN %d AND %d",
										avgMood.R-MOOD_RANGE,avgMood.R+MOOD_RANGE,
										avgMood.G-MOOD_RANGE,avgMood.G+MOOD_RANGE,
										avgMood.B-MOOD_RANGE,avgMood.B+MOOD_RANGE)
	return getSongsGeneric(sql)
}

func GetSongs() ([]dt.Song, error) {
	return getSongsGeneric("SELECT * FROM song")
}

func GetSongsByChaos(num int) ([]dt.Song, error) {
	return getSongsGeneric(fmt.Sprintf("SELECT * FROM song ORDER BY RANDOM() LIMIT %d", num))
}


func getSongsGeneric(query string) ([]dt.Song, error){

	songs := []dt.Song{}
	f := func(db *sql.DB) error {
		rows, err := db.Query(query)
		if err != nil {
			return err
		}
		defer rows.Close()
		songs = convSongs(rows)
		return nil
	}
	if err := doTransaction(f); err != nil {
		return nil, err
	}
	return songs, nil
}

func getVotesGeneric(query string) ([]dt.Vote, error){

	votes := []dt.Vote{}
	f := func(db *sql.DB) error {
		rows, err := db.Query(query)
		if err != nil {
			return err
		}
		defer rows.Close()
		votes = convVotes(rows)
		return nil
	}
	if err := doTransaction(f); err != nil {
		return nil, err
	}
	return votes, nil
}



func GetSongLove(user dt.User, song dt.Song) (int, error){
	like :=0;
	songs, err := getVotesGeneric(fmt.Sprintf("SELECT * FROM vote WHERE user IS %d AND song IS %d",user.Id,song.Id))
	for i := range songs {
		if songs[i].Like {
			like++
		} else{
			like--
		}
	}
	if like>5 {
		like=5
	}else if like<0{
		like=0
	}
	return like, err
}

func GetCrowdFavs(num int) {

}


func convSongs(rows *sql.Rows) []dt.Song {
	songs := []dt.Song{}
	for rows.Next() {
		song := dt.Song{}
		rows.Scan(&song.Id,&song.Title,&song.Album,&song.Artist,&song.Genre)
		songs = append(songs,song)
	}
	return songs
}

func convVotes(rows *sql.Rows) []dt.Vote {
	votes := []dt.Vote{}
	for rows.Next() {
		vote := dt.Vote{}
		var r , g, b int
		rows.Scan(&vote.Id,&vote.SongId,&vote.UserId,&vote.Like,&r,&g,&b)
		vote.Mood = dt.Mood{R: r, G: g, B: b}
		votes = append(votes,vote)
	}
	return votes
}

type DBCallback func(*sql.DB) error
func doTransaction(call DBCallback) error {
	db, err := sql.Open("sqlite3", DB_PATH)
	if err != nil {
		return err
	}
	defer db.Close()
	tx, err := db.Begin();
	if err != nil {
		return err
	}
	
	if err := call(db); err != nil {
		tx.Rollback()
		return err
	} else {
		tx.Commit()
		return nil
	}
}