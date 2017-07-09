package main

import (
	"encoding/json"
	"io/ioutil"
	"os"
)

// Root Dir
var Root string

// configFile to save MainConfig to
var configFile = "config.json"

// MainConfig holds all basic configuration
var MainConfig struct {
	NametagFile     string `json:"Nametag File"`            // File to store nametags
	PrinterFile     string `json:"Printer File"`            // File to store printers
	ImagesDir       string `json:"Images Directory"`        // Directory to store generated images
	OpenScadScript  string `json:"OpenSCAD Script"`         // Openscad script location
	StlDir          string `json:"Stl Direcotory"`          // Directory to store generated stl files
	GcodeDir        string `json:"Gcode Directory"`         // Directory to store generated gcode files
	SlicerConfigDir string `json:"Slicer Config Directory"` // Directory to store slicer config files
	OpenScadPath    string `json:"OpenSCAD Path"`           // Path to use to run OpenSCAD
	SlicerPath      string `json:"Slic3r Path"`             // path to use to run Slic3r
}

func initMainDefaults() {
	MainConfig.NametagFile = "config/nametags.json"
	MainConfig.PrinterFile = "config/printers.json"
	MainConfig.ImagesDir = "assets/images/nametags/"
	MainConfig.OpenScadScript = "openscad/"
	MainConfig.StlDir = "stl/"
	MainConfig.GcodeDir = "gcode/"
	MainConfig.SlicerConfigDir = "config/"
	MainConfig.OpenScadPath = "openscad"
	MainConfig.SlicerPath = "slic3r"
}

func loadMain() {
	confPath := Root + configFile

	if _, err := os.Stat(confPath); os.IsNotExist(err) {
		initMainDefaults()
		saveMain()
	} else {
		in, err := ioutil.ReadFile(Root + configFile)
		if err != nil {
			panic(err)
		}
		json.Unmarshal(in, &MainConfig)
	}
}

func saveMain() {
	out, jerr := json.MarshalIndent(MainConfig, "", "  ")

	if jerr != nil {
		Error.Println(jerr)
	}

	Main.Println(out)

	err := ioutil.WriteFile(Root+configFile, out, 0644)
	if err != nil {
		panic(err)
	}
}

func loadNametags() {
	in, err := ioutil.ReadFile(Root + MainConfig.NametagFile)
	if err != nil {
		panic(err)
	}

	nametagsMux.Lock()
	json.Unmarshal(in, &nametags)

	maxID := 0

	for i := range nametags {
		if nametags[i].ID > maxID {
			maxID = nametags[i].ID
		}

		nametags[i].Status = NIdle
		//nametags[i].Processing = false
		nametags[i].PrinterID = 0
	}
	nametagsMux.Unlock()

	CurrentID = maxID

	//Main.Println(nametags)
}

func saveNametags() {

	nametagsMux.Lock()
	out, jerr := json.MarshalIndent(nametags, "", "  ")
	nametagsMux.Unlock()

	if jerr != nil {
		Error.Println(jerr)
	}

	err := ioutil.WriteFile(Root+MainConfig.NametagFile, out, 0644)
	if err != nil {
		panic(err)
	}
}

func loadPrinters() {
	in, err := ioutil.ReadFile(Root + MainConfig.PrinterFile)
	if err != nil {
		panic(err)
	}
	json.Unmarshal(in, &printers)

	for i := range printers {
		printers[i].Status = PIdle
		//printers[i].Available = true
		printers[i].NametagID = 0
	}

	//Main.Println(printers)
}

func savePrinters() {

	out, jerr := json.MarshalIndent(printers, "", "  ")

	if jerr != nil {
		Error.Println(jerr)
	}

	err := ioutil.WriteFile(Root+MainConfig.PrinterFile, out, 0644)
	if err != nil {
		panic(err)
	}
}
