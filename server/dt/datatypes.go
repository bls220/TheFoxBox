
package dt

import "math"

// This file represents the datatypes that are important to this project.

type Mood struct {
	R, G, B int
}
func (m Mood) Dist(o Mood) float64 {
	r, g, b := float64(m.R-o.R), float64(m.G-o.G), float64(m.B-o.B)
	
	return math.Sqrt(r*r + g*g + b*b)
}

type User struct {
	Id int
	Name string
	CurMood Mood
}

// Represents a set of users in a room
type Room struct {
	InRoom map[string]*User
}
func (ro Room) AverageMood() Mood {
	r, g, b := 0.0, 0.0, 0.0
	for _, v := range ro.InRoom {
		m := v.CurMood
		r += float64(m.R)
		g += float64(m.G)
		b += float64(m.B)
	}
	
	l := float64(len(ro.InRoom))
	return Mood{int(r/l), int(g/l), int(b/l)}
}

type Song struct {
	Id int
	Artist, Album, Title string
}

type Vote struct {
	Id int
	mood Mood
	song Song
	user User
	like bool
}
