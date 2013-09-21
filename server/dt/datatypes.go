
package dt

// This file represents the datatypes that are important to this project.

type Mood struct {
	r, g, b int
}
func (m Mood) Dist(o Mood) float64 {
	r, g, b := float64(m.r-o.r), float64(m.g-o.g), float64(m.b-o.b)
	
	return math.Sqrt(r*r + g*g + b*b)
}

type User struct {
	id int
	name string
	curMood Mood
}

// Represents a set of users in a room
type Room struct {
	inRoom map[string]*User
}
func (r Room) AverageMood() Mood {
	r, g, b := 0.0, 0.0, 0.0
	for _, v := range r.inRoom {
		m := v.curMood
		r += m.r
		g += m.g
		b += m.g
	}
	
	l := len(r.inRoom)
	return Mood{int(r/l), int(g/l), int(b/l)}
}



