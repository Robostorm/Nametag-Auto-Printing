package main

import (
	"encoding/json"
	"io/ioutil"
)

// Dirs and paths
const (
	NametagFile  = "config/nametags.json"
	PrinterFile  = "config/printers.json"
	ImagesDir    = "assets/images/nametags/"
	OpenScadDir  = "openscad/"
	StlDir       = "stl/"
	GcodeDir     = "gcode/"
	ConfigDir    = "config/"
	OpenScadPath = "openscad"
	SlicerPath   = "slic3r"
)

// Root Dir
var Root string

func loadNametags() {
	in, err := ioutil.ReadFile(Root + NametagFile)
	if err != nil {
		panic(err)
	}
	json.Unmarshal(in, &nametags)

	for i := range nametags {
		nametags[i].Status = NIdle
		nametags[i].Processing = false
		nametags[i].PrinterID = 0
	}

	Main.Println(nametags)
}

func saveNametags() {

	out, jerr := json.MarshalIndent(nametags, "", "  ")

	if jerr != nil {
		Error.Println(jerr)
	}

	err := ioutil.WriteFile(Root+NametagFile, out, 0644)
	if err != nil {
		panic(err)
	}
}

func loadPrinters() {
	in, err := ioutil.ReadFile(Root + PrinterFile)
	if err != nil {
		panic(err)
	}
	json.Unmarshal(in, &printers)

	for i := range printers {
		printers[i].Status = PIdle
		printers[i].Available = true
		printers[i].NametagID = 0
	}

	Main.Println(printers)
}

func savePrinters() {

	out, jerr := json.MarshalIndent(printers, "", "  ")

	if jerr != nil {
		Error.Println(jerr)
	}

	err := ioutil.WriteFile(Root+PrinterFile, out, 0644)
	if err != nil {
		panic(err)
	}
}
