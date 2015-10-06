package main

import (
	"encoding/json"
	"io/ioutil"
)

var (
	nametagFile = "config/nametags.json"
	printerFile = "config/printers.json"
)

func loadNametags() {
	in, err := ioutil.ReadFile(nametagFile)
	if err != nil {
		panic(err)
	}

	json.Unmarshal(in, &nametags)
	Info.Println(nametags)
}

func saveNametags() {

	out, jerr := json.MarshalIndent(nametags, "", "  ")

	if jerr != nil {
		Error.Println(jerr)
	}

	err := ioutil.WriteFile(nametagFile, out, 0644)
	if err != nil {
		panic(err)
	}
}

func loadPrinters() {
	in, err := ioutil.ReadFile(printerFile)
	if err != nil {
		panic(err)
	}

	json.Unmarshal(in, &printers)
	Info.Println(printers)
}

func savePrinters() {

	out, jerr := json.MarshalIndent(printers, "", "  ")

	if jerr != nil {
		Error.Println(jerr)
	}

	err := ioutil.WriteFile(printerFile, out, 0644)
	if err != nil {
		panic(err)
	}
}
