
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
	Admin bool
}

// Decoupling is important sometimes!
type Room interface {
	GetUserIdsInRoom() []int
	AverageMood() Mood
}

type Song struct {
	Id int
	Artist, Album, Title, Genre string
}

type Vote struct {
	Id int
	Mood Mood
	Song Song
	User User
	Like bool
}
