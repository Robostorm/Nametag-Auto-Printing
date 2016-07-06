package main

import (
	"fmt"
	"os/exec"
	"strings"
)

// States for a nametag
const (
	NIdle      = "Idle"
	NRendering = "Rendering"
	NSlicing   = "Slicing"
	NUploading = "Uploading"
	NPrinting  = "Printing"
	NDone      = "Done"
	NErrored   = "Errored!"
)

// CurrentID is the current max ID of the nametags
var CurrentID = 0

// Nametag to be printed. Will appear in manager in the order defined here. JSON names that start with a _ will not be editable
type Nametag struct {
	ID     int    `json:"ID"`      // Unique ID of the nametag
	Name   string `json:"Name"`    // Name on the nametag
	Status string `json:"_Status"` // Status of the nametag
	//Processing bool   `json:"Processing"` // Processing or not
	PrinterID int `json:"Printer ID"` // Printer id that nametag will print on
}

func (nametag *Nametag) generateID() int {
	CurrentID++
	nametag.ID = CurrentID
	return nametag.ID
}

/*
func (nametag *Nametag) findPrinter() bool {
	for i := range printers {
		if printers[i].Available && printers[i].Active {
			nametag.PrinterID = printers[i].ID
			printers[i].NametagID = nametag.ID
			printers[i].Available = false
			return true
		}
	}
	return false
}
*/
func (nametag *Nametag) exists() bool {
	found := false
	for i := range nametags {
		if nametags[i].ID == nametag.ID {
			found = true
			break
		}
	}
	return found
}

func sanitize(name string) string {
	return strings.Replace(name, " ", "", -1)
}

func previewNametag(name string) (string, error) {

	zoom := 70
	if len(name) > 8 {
		zoom = 120
	} else if len(name) > 5 {
		zoom = 105
	}

	// scadArgs := fmt.Sprintf(" -o %s%s.png -D name=\"%s\" -D chars=%d "+"--camera=0,0,0,0,0,0,%d --imgsize=512,400 %sname.scad", Root+MainConfig.imagesDir, name, name, len(name), zoom, Root+MainConfig.MainConfig.OpenScadScript)
	//
	// cmd := MainConfig.openscadPath + scadArgs
	//
	// Manager.Println("Running:")
	// Manager.Println(cmd)
	// parts := strings.Fields(cmd)
	// head := parts[0]
	// parts = parts[1:len(parts)]

	//out, err := exec.Command(head, parts...).Output()

	sname := strings.Replace(name, "\\", "\\\\\\\\", -1)
	sname = strings.Replace(sname, "\"", "\\\"", -1)

	Manager.Printf("sname: %s", sname)

	args := []string{
		fmt.Sprintf("-o%s%s.png", Root+MainConfig.ImagesDir, name),
		fmt.Sprintf("-D name=\"%s\"", sname),
		fmt.Sprintf("-D chars=%d "+"", len(name)),
		fmt.Sprintf("--camera=0,0,0,0,0,0,%d", zoom),
		fmt.Sprintf("--imgsize=512,400"),
		fmt.Sprintf("%sname.scad", Root+MainConfig.OpenScadScript),
	}

	cmd := exec.Command(MainConfig.OpenScadPath, args...)

	Manager.Printf("Executing: %s", cmd.Args)

	out, err := cmd.CombinedOutput()

	if err != nil {
		Error.Printf("%s", err)
	}
	Manager.Println("Standard Out:")
	Manager.Printf("%s", out)

	return fmt.Sprintf("%s/%s", Root+MainConfig.ImagesDir, name), nil
}
