package main

import (
	"fmt"
	"math/rand"
	"os/exec"
	"strings"
)

var (
	imagesDir = "assets/images/"
	scadDir   = "openscad/"
	scadPath  = "openscad"
)

// Nametag to be printed
type Nametag struct {
	ID        int    // Unique ID of the nametag, ganerated from current date and time
	Name      string // Name on the nametag
	StlPath   string // Path to the stl
	GcodePath string // Path to the gcode
	ImagePath string // Path to the image
	Rendered  bool   // Exported from Scad or not
	Sliced    bool   // Sliced or not
	Uploaded  bool   // Uploaded or not
	Printed   bool   // Printed or not
	PrinterID int64  // Printer id that nametag will print on
}

func (nametag *Nametag) generateID() int {
	done := false

	for !done {
		id := rand.Intn(999)

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
