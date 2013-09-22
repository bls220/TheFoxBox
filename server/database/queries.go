package database

import (
	_ "github.com/mattn/go-sqlite3"
	"../dt"
	"database/sql"
	"strconv"
)

const MOOD_RANGE int = 10;

func GetUser(name string) {
	
}
func AddUser(user dt.User) error{
	f := func(db *sql.DB) error {
		_, err := db.Exec("INSERT into user(name,admin) values(?,?)", user.Name, user.Admin);
		return err
	}
	return doTransaction(f)
}

func AddSongs(songs []dt.Song) error {
	f := func(db *sql.DB) error {
		for _, x := range songs {
			if _, err := db.Exec("INSERT into song(title,album,artist) values(?,?,?)",x.Title,x.Album,x.Artist); err != nil {
				return err
			}
		}
		return nil
	}
	return doTransaction(f)
}

func AddVote(vote dt.Vote) error {
	f := func(db *sql.DB) error {
		_, err := db.Exec("INSERT into vote(song,user,like,r,g,b) values(?,?,?,?,?,?)",vote.SongId,vote.UserId,vote.Like,vote.Mood.R,vote.Mood.G,vote.Mood.B);
		return err
	}
	return doTransaction(f)
}

func GetSongsByString(search string) ([]dt.Song, error) {
	return getSongsGeneric("SELECT * FROM song WHERE title LIKE ? LIMIT 100", "%" + search + "%")
}


func GetSongsByMood(mood dt.Mood, count int) ([]dt.Song, error) {
	return getSongsGeneric("SELECT * FROM vote WHERE like=\"1\" AND r BETWEEN ? AND ? AND g BETWEEN ? AND ? AND b BETWEEN ? AND ? LIMIT ? ORDER BY RANDOM()",
										mood.R-MOOD_RANGE,mood.R+MOOD_RANGE,
										mood.G-MOOD_RANGE,mood.G+MOOD_RANGE,
										mood.B-MOOD_RANGE,mood.B+MOOD_RANGE, count)
}

func GetSongsByRoom(room dt.Room, count int) ([]dt.Song, error) {
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
				"avgr BETWEEN ? AND ? AND avgg BETWEEN ? AND ? AND avgb BETWEEN ? AND ? LIMIT ? ORDER BY RANDOM()"
	return getSongsGeneric(sql,avgMood.R-MOOD_RANGE,avgMood.R+MOOD_RANGE,
										avgMood.G-MOOD_RANGE,avgMood.G+MOOD_RANGE,
										avgMood.B-MOOD_RANGE,avgMood.B+MOOD_RANGE, count)
}

func GetSongs() ([]dt.Song, error) {
	return getSongsGeneric("SELECT * FROM song")
}

func GetSongsByChaos(num int) ([]dt.Song, error) {
	return getSongsGeneric("SELECT * FROM song ORDER BY RANDOM() LIMIT ?", num)
}


func getSongsGeneric(query string, args...interface{}) ([]dt.Song, error){

	songs := []dt.Song{}
	f := func(db *sql.DB) error {
		rows, err := db.Query(query, args...)
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

func getVotesGeneric(query string, args...interface{}) ([]dt.Vote, error){

	votes := []dt.Vote{}
	f := func(db *sql.DB) error {
		rows, err := db.Query(query, args...)
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
	votes, err := getVotesGeneric("SELECT * FROM vote WHERE user IS ? AND song IS ?",user.Id,song.Id)
	for i := range votes {
		if votes[i].Like {
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

func GetBestFavs(num int) ([]dt.Song, error) {
	
	votes, err := getVotesGeneric("SELECT * SUM(like) FROM vote GROUP BY song LIMIT ?", num)
	if err != nil{
		return nil, err
	}

	return GetSongsFromVotes(votes)

}

func GetSongsFromVotes(votes []dt.Vote) ([]dt.Song, error){
	if len(votes) == 0 {
		return []dt.Song{}, nil
	}
	
	idStr := `"` + strconv.Itoa(votes[0].SongId) + `"`
	for i := range votes {
		idStr += `,"` + strconv.Itoa(votes[i].SongId) + `"`
	}

	sql := `SELECT * FROM song WHERE id IN (` + idStr + `)`

	return getSongsGeneric(sql)
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