package main

import (
	"fmt"
	s1 "strings"
)

var p = fmt.Println

func main() {

	p("Contains:  ", s1.Contains("test", "es"))
	p("Count:     ", s1.Count("test", "t"))
	p("HasPrefix: ", s1.HasPrefix("test", "te"))
	p("HasSuffix: ", s1.HasSuffix("test", "st"))
	p("Index:     ", s1.Index("test", "e"))
	p("Join:      ", s1.Join([]string{"a", "b"}, "-"))
	p("Repeat:    ", s1.Repeat("a", 5))
	p("Replace:   ", s1.Replace("foo", "o", "0", -1))
	p("Replace:   ", s1.Replace("foo", "o", "0", 1))
	p("Split:     ", s1.Split("a-b-c-d-e", "-"))
	p("ToLower:   ", s1.ToLower("TEST"))
	p("ToUpper:   ", s1.ToUpper("test"))
}
