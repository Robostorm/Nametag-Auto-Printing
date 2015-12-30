package main

import (
	"bytes"
	"errors"
	"fmt"
	"io/ioutil"
	"log"
	"mime/multipart"
	"net/http"
	"net/http/httputil"
	"os"
	"os/exec"
	"strings"
)

// States for a printer
const (
	PIdle      = "Idle"
	PRendering = "Rendering"
	PSlicing   = "Slicing"
	PUploading = "Uploading"
	PPrinting  = "Printing"
	PDone      = "Done"
	PErrored   = "Errored!"
)

// Printer for printing nametags
type Printer struct {
	ID     int    `json:"ID"`      // Unique ID of the printer
	Name   string `json:"Name"`    // Readable name for the printer
	Status string `json:"_Status"` // Status of the printer
	Active bool   `json:"Active"`  // Active or not
	//Available  bool   `json:"Available"`     // Available or not
	NametagID  int    `json:"Nametag ID"`    // Nametag ID that is currently printing
	IP         string `json:"IP"`            // IP for the printer
	APIKey     string `json:"API Key"`       // API Key to use for the printer
	SlicerConf string `json:"Slicer Config"` // Slicer config path
}

func (printer *Printer) generateID() int {

	for id := 10; ; id++ {
		//Manager.Printf("Trying ID: %d", id)
		unique := true
		for i := range printers {
			if id == printers[i].ID {
				unique = false
				break
			}
		}
		if unique {
			printer.ID = id
			break
		}
	}

	printer.Status = PIdle
	//Manager.Println(printer.ID)
	return printer.ID
}

func (printer *Printer) renderNametag(id int) (err error) {

	_, nametag := findNametag(id)

	//Manager.Printf("Rendering Nametag %d for Printer %d", nametag.ID, printer.ID)

	//nametag.Status = NRendering
	//printer.Status = PRendering

	scadArgs := fmt.Sprintf(" -o %s%s%d.stl -D name=\"%s\" -D chars=%d %s%sname.scad", Root, StlDir, nametag.ID, nametag.Name, len(nametag.Name), Root, OpenScadDir)

	cmd := OpenScadPath + scadArgs

	//Manager.Println("Running:")
	//Manager.Println(cmd)
	parts := strings.Fields(cmd)
	head := parts[0]
	parts = parts[1:len(parts)]

	//out, err := exec.Command(head, parts...).CombinedOutput()
	_, err = exec.Command(head, parts...).CombinedOutput()
	if err != nil {
		Error.Printf("%s", err)
	}
	//Manager.Printf("Standard Out and Error:\n%s", out)

	return nil
}

func (printer *Printer) sliceNametag(id int) error {

	_, nametag := findNametag(id)

	if nametag == nil {
		return errors.New("Could not find nametag!")
	}

	//Manager.Printf("Slicing Nametag %d for Printer %d", nametag.ID, printer.ID)

	//nametag.Status = NSlicing
	//printer.Status = PSlicing

	slicerArgs := fmt.Sprintf(" -o %s%s%d.gcode --load %s%s%s %s%s%d.stl", Root, GcodeDir, nametag.ID, Root, ConfigDir, printer.SlicerConf, Root, StlDir, nametag.ID)

	cmd := SlicerPath + slicerArgs

	//Manager.Println("Running:")
	//Manager.Println(cmd)
	parts := strings.Fields(cmd)
	head := parts[0]
	parts = parts[1:len(parts)]

	//out, err := exec.Command(head, parts...).CombinedOutput()
	_, err := exec.Command(head, parts...).CombinedOutput()
	if err != nil {
		Error.Printf("%s", err)
	}
	//Manager.Printf("Standard Out and Error:\n%s", out)

	return nil
}

func (printer *Printer) uploadNametag(id int) error {

	_, nametag := findNametag(id)

	if nametag == nil {
		return errors.New("Could not find nametag!")
	}

	//Manager.Printf("Uploading Nametag %d to Printer %d", nametag.ID, printer.ID)

	//nametag.Status = NUploading
	//printer.Status = PUploading

	uri := fmt.Sprintf("http://%s/api/files/local", printer.IP)

	file, err := os.Open(fmt.Sprintf("%s%d.gcode", GcodeDir, nametag.ID))
	if err != nil {
		return err
	}
	fileContents, err := ioutil.ReadAll(file)
	if err != nil {
		return err
	}
	fi, err := file.Stat()
	if err != nil {
		return err
	}
	file.Close()

	body := new(bytes.Buffer)
	writer := multipart.NewWriter(body)
	part, err := writer.CreateFormFile("file", fi.Name())
	if err != nil {
		return err
	}
	part.Write(fileContents)
	_ = writer.WriteField("print", "true")
	err = writer.Close()
	if err != nil {
		return err
	}
	request, err := http.NewRequest("POST", uri, body)
	if err != nil {
		return err
	}
	request.Header.Add("X-Api-Key", printer.APIKey)
	request.Header.Set("Content-Type", writer.FormDataContentType())
	_, err = httputil.DumpRequest(request, true)
	if err == nil {
		//Manager.Println(data)
	} else {
		Error.Println(err)
	}
	client := &http.Client{}
	resp, err := client.Do(request)
	if err != nil {
		return err
	}
	body = &bytes.Buffer{}
	_, err = body.ReadFrom(resp.Body)
	if err != nil {
		log.Println(err)
	}
	resp.Body.Close()

	return nil
}
