package main

import "math/rand"

// Printer for printing nametags
type Printer struct {
	ID         int    // Unique ID of the printer
	Name       string // Readable name for the printer
	Nametag    int    // Nametag ID that is currently printing
	IP         string // IP for the printer
	APIKey     string // API Key to use for the printer
	SlicerConf string // Slicer config path
	Active     bool   // Active or not
	Printing   bool   // Printing or not
	Slicing    bool   // Slicing or not
	Uploading  bool   // Uploading or not
}

func (printer *Printer) generateID() int {
	done := false

	for !done {
		id := rand.Intn(999)

		found := false
		for _, p := range nametags {
			if p.ID == id {
				found = true
			}
		}

		if !found {
			done = true
			printer.ID = id
		}
	}

	Info.Println(printer.ID)
	return printer.ID
}
