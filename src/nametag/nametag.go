package main

import (
	"fmt"
	"math/rand"
	"os/exec"
	"strings"
)

var (
	imagesDir = "assets/images/nametags/"
	scadDir   = "openscad/"
	scadPath  = "openscad"
)

// Nametag to be printed. Will appear in manager in the order defined here
type Nametag struct {
	ID        int    `json:"ID"`         // Unique ID of the nametag
	Name      string `json:"Name"`       // Name on the nametag
	Rendered  bool   `json:"Rendered"`   // Exported from Scad or not
	Sliced    bool   `json:"Sliced"`     // Sliced or not
	Uploaded  bool   `json:"Uploaded"`   // Uploaded or not
	Printed   bool   `json:"Printed"`    // Printed or not
	PrinterID int64  `json:"Printer ID"` // Printer id that nametag will print on
	StlPath   string `json:"Stl Path"`   // Path to the stl
	GcodePath string `json:"Gcode Path"` // Path to the gcode
}

func (nametag *Nametag) generateID() int {
	done := false

	for !done {
		// Zero is special
		id := rand.Intn(998) + 1

		found := false
		for _, n := range nametags {
			if n.ID == id {
				found = true
			}
		}

		if !found {
			done = true
			nametag.ID = id
		}
	}

	Info.Println(nametag.ID)
	return nametag.ID
}

func previewNametag(name string) (string, error) {

	zoom := 100
	if len(name) > 8 {
		zoom = 150
	} else if len(name) > 5 {
		zoom = 130
	}

	scadArgs := fmt.Sprintf(" -o %s%s.png -D name=\"%s\" -D chars=%d "+"--camera=0,0,0,0,0,0,%d %sname.scad", root+imagesDir, name, name, len(name), zoom, root+scadDir)

	cmd := scadPath + scadArgs

	Info.Println("Running:")
	Info.Println(cmd)
	parts := strings.Fields(cmd)
	head := parts[0]
	parts = parts[1:len(parts)]

	out, err := exec.Command(head, parts...).Output()
	if err != nil {
		Error.Printf("%s", err)
	}
	Info.Println("Standard Out:")
	Info.Printf("%s", out)

	return fmt.Sprintf("%s/%s", root+imagesDir, name), nil
}
