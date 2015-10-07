package main

import (
	"fmt"
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
	ID         int    `json:"ID"`         // Unique ID of the nametag
	Name       string `json:"Name"`       // Name on the nametag
	Status     string `json:"_Status"`    // Status of the nametag
	Processing bool   `json:"Processing"` // Processing or not
	PrinterID  int    `json:"Printer ID"` // Printer id that nametag will print on
	StlPath    string `json:"-"`          // Path to the stl
	GcodePath  string `json:"-"`          // Path to the gcode
}

func (nametag *Nametag) generateID() int {

	for id := 100; ; id++ {
		Info.Printf("Trying ID: %d", id)
		unique := true
		for i := range nametags {
			if id == nametags[i].ID {
				unique = false
				break
			}
		}
		if unique {
			nametag.ID = id
			break
		}
	}

	Info.Println(nametag.ID)

	return nametag.ID
}

func (nametag *Nametag) findPrinter() bool {
	Info.Printf("Finding Printer for %d\n", nametag.ID)
	for i := range printers {
		Info.Printf("Considering %d\n", printers[i].ID)
		if printers[i].Available && printers[i].Active {
			Info.Println("Printer is available")
			nametag.PrinterID = printers[i].ID
			printers[i].NametagID = nametag.ID
			printers[i].Available = false
			return true
		}
		Info.Println("Printer is not available")
	}
	return false
}

func (nametag *Nametag) process() {
	nametag.Processing = true
	Info.Printf("Processing %d for %d\n", nametag.ID, nametag.PrinterID)
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
