package main

import (
	"bytes"
	"errors"
	"fmt"
	"io/ioutil"
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

// PrinterColumns are the Columns that should be shown in the interface for printers
var PrinterColumns = []Column{
	{
		Name:     "id",
		Label:    "ID",
		Type:     "integer",
		Editable: false,
	},
	{
		Name:     "name",
		Label:    "Name",
		Type:     "string",
		Editable: true,
	},
	{
		Name:     "status",
		Label:    "Status",
		Type:     "string",
		Editable: false,
	},
	{
		Name:     "active",
		Label:    "Active",
		Type:     "boolean",
		Editable: true,
	},
	{
		Name:     "selectable",
		Label:    "Selectable",
		Type:     "boolean",
		Editable: true,
	},
	{
		Name:     "nametag-id",
		Label:    "Nametag ID",
		Type:     "integer",
		Editable: false,
	},
	{
		Name:     "color",
		Label:    "Color",
		Type:     "string",
		Editable: true,
	},
	{
		Name:     "ip",
		Label:    "IP",
		Type:     "string",
		Editable: true,
	},
	{
		Name:     "api-key",
		Label:    "API Key",
		Type:     "string",
		Editable: true,
	},
	{
		Name:     "slicer-config",
		Label:    "Slicer Config",
		Type:     "string",
		Editable: true,
	},
}

// Printer for printing nametags
type Printer struct {
	ID         int    `json:"id"`            // Unique ID of the printer
	Name       string `json:"name"`          // Readable name for the printer
	Status     string `json:"status"`        // Status of the printer
	Active     bool   `json:"active"`        // Active or not
	Selectable bool   `json:"selectable"`    // Selectable or not
	NametagID  int    `json:"nametag-id"`    // Nametag ID that is currently printing
	Color      string `json:"color"`         // The color that the printer prints
	IP         string `json:"ip"`            // IP for the printer
	APIKey     string `json:"api-key"`       // API Key to use for the printer
	SlicerConf string `json:"slicer-config"` // Slicer config path
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

	Manager.Printf("Rendering Nametag %d for Printer %d", nametag.ID, printer.ID)

	args := []string{
		fmt.Sprintf("-o%s%d.stl", Root+MainConfig.StlDir, nametag.ID),
		fmt.Sprintf("-D name=\"%s\"", nametag.Name),
		fmt.Sprintf("-D chars=%d "+"", len(nametag.Name)),
		fmt.Sprintf("%sname.scad", Root+MainConfig.OpenScadScript),
	}

	CurrentCommand = MainConfig.OpenScadPath + " " + strings.Join(args, " ")

	out, err := exec.Command(MainConfig.OpenScadPath, args...).CombinedOutput()
	//_, err = exec.Command(head, parts...).CombinedOutput()

	Debug.Printf("Standard Out and Error:\n%s", out)

	if err != nil {
		return err
		//Error.Printf("%s", err)
	}

	CurrentCommand = ""

	return nil
}

func (printer *Printer) sliceNametag(id int) error {

	_, nametag := findNametag(id)

	if nametag == nil {
		return errors.New("Could not find nametag")
	}

	Manager.Printf("Slicing Nametag %d for Printer %d", nametag.ID, printer.ID)

	//nametag.Status = NSlicing
	//printer.Status = PSlicing

	slicerArgs := fmt.Sprintf(" -o %s%s%d.gcode --load %s%s%s %s%s%d.stl", Root, MainConfig.GcodeDir, nametag.ID, Root, MainConfig.SlicerConfigDir, printer.SlicerConf, Root, MainConfig.StlDir, nametag.ID)

	cmd := MainConfig.SlicerPath + slicerArgs

	CurrentCommand = cmd

	//Manager.Println("Running:")
	//Manager.Println(cmd)
	parts := strings.Fields(cmd)
	head := parts[0]
	parts = parts[1:len(parts)]

	out, err := exec.Command(head, parts...).CombinedOutput()
	//_, err := exec.Command(head, parts...).CombinedOutput()

	Debug.Printf("Standard Out and Error:\n%s", out)

	if err != nil {
		return err
	}

	CurrentCommand = ""

	return nil
}

func (printer *Printer) uploadNametag(id int) error {

	_, nametag := findNametag(id)

	if nametag == nil {
		return errors.New("Could not find nametag")
	}

	Manager.Printf("Uploading Nametag %d to Printer %d", nametag.ID, printer.ID)

	//nametag.Status = NUploading
	//printer.Status = PUploading

	CurrentCommand = "Uploading " + nametag.Name + " to " + printer.Name

	uri := fmt.Sprintf("http://%s/api/files/local", printer.IP)

	// Open Gcode file
	file, err := os.Open(fmt.Sprintf("%s%d.gcode", Root+MainConfig.GcodeDir, nametag.ID))
	if err != nil {
		return err
	}

	// Get file contents
	fileContents, err := ioutil.ReadAll(file)
	if err != nil {
		return err
	}
	// Get file stats
	fi, err := file.Stat()
	if err != nil {
		return err
	}
	file.Close()

	// Read file
	body := new(bytes.Buffer)

	// Read file into writer for sending
	writer := multipart.NewWriter(body)
	part, err := writer.CreateFormFile("file", fi.Name())
	if err != nil {
		return err
	}

	// Write file
	part.Write(fileContents)

	// Create print header
	//_ = writer.WriteField("print", "true")
	err = writer.Close()
	if err != nil {
		return err
	}

	Debug.Println("Creating request")
	// Create POST request
	request, err := http.NewRequest("POST", uri, body)
	if err != nil {
		return err
	}

	Debug.Println("Adding headers")
	// Add headers to POST request
	request.Header.Add("X-Api-Key", printer.APIKey)
	request.Header.Set("Content-Type", writer.FormDataContentType())
	_, err = httputil.DumpRequest(request, true)
	if err != nil {
		return err
	}

	Debug.Println("Doing request")
	// Do request
	client := &http.Client{}
	resp, err := client.Do(request)
	if err != nil {
		//Warning.Println(err)
		printer.Active = false
		return err
	}
	defer resp.Body.Close()

	Debug.Println("Reading response")
	// Read response
	body = &bytes.Buffer{}
	_, err = body.ReadFrom(resp.Body)
	if err != nil {
		return err
	}

	Manager.Println(resp.Status)
	Manager.Println(body)

	if resp.StatusCode != 201 {
		return errors.New("Wrong status code: " + resp.Status)
	}

	CurrentCommand = ""

	return nil
}

func (printer *Printer) selectNametag(id int) error {

	_, nametag := findNametag(id)

	if nametag == nil {
		return errors.New("Could not find nametag")
	}

	Manager.Printf("Selecting Nametag %d on Printer %d", nametag.ID, printer.ID)

	//nametag.Status = NUploading
	//printer.Status = PUploading

	CurrentCommand = "Selecting " + nametag.Name + " no " + printer.Name

	uri := fmt.Sprintf("http://%s/api/files/local/%d.gcode", printer.IP, id)

	var json = []byte(`{"command":"select"}`)

	// Create POST request
	request, err := http.NewRequest("POST", uri, bytes.NewBuffer(json))
	if err != nil {
		return err
	}

	// Add headers to POST request
	request.Header.Add("X-Api-Key", printer.APIKey)
	request.Header.Set("Content-Type", "application/json")
	_, err = httputil.DumpRequest(request, true)
	if err != nil {
		return err
	}

	// Do request
	client := &http.Client{}
	resp, err := client.Do(request)
	if err != nil {
		//Warning.Println(err)
		printer.Active = false
		return err
	}
	defer resp.Body.Close()

	Manager.Println(resp.Status)

	if resp.StatusCode != 204 {
		return errors.New("Wrong status code: " + resp.Status)
	}

	CurrentCommand = ""

	return nil
}

func (printer *Printer) print() error {

	Manager.Printf("Printing on Printer %d", printer.ID)

	//nametag.Status = NUploading
	//printer.Status = PUploading

	CurrentCommand = "Printing on" + printer.Name

	uri := fmt.Sprintf("http://%s/api/job", printer.IP)

	var json = []byte(`{"command":"start"}`)

	// Create POST request
	request, err := http.NewRequest("POST", uri, bytes.NewBuffer(json))
	if err != nil {
		return err
	}

	// Add headers to POST request
	request.Header.Add("X-Api-Key", printer.APIKey)
	request.Header.Set("Content-Type", "application/json")
	_, err = httputil.DumpRequest(request, true)
	if err != nil {
		return err
	}

	// Do request
	client := &http.Client{}
	resp, err := client.Do(request)
	if err != nil {
		//Warning.Println(err)
		printer.Active = false
		return err
	}
	defer resp.Body.Close()

	Manager.Println(resp.StatusCode)

	if resp.StatusCode != 204 {
		return errors.New("Wrong status code: " + resp.Status)
	}

	CurrentCommand = ""

	return nil
}

func (printer *Printer) cancel() error {

	Manager.Printf("Canceling on Printer %d", printer.ID)

	//nametag.Status = NUploading
	//printer.Status = PUploading

	CurrentCommand = "Canceling on" + printer.Name

	uri := fmt.Sprintf("http://%s/api/job", printer.IP)

	var json = []byte(`{"command":"cancel"}`)

	// Create POST request
	request, err := http.NewRequest("POST", uri, bytes.NewBuffer(json))
	if err != nil {
		return err
	}

	// Add headers to POST request
	request.Header.Add("X-Api-Key", printer.APIKey)
	request.Header.Set("Content-Type", "application/json")
	_, err = httputil.DumpRequest(request, true)
	if err != nil {
		return err
	}

	// Do request
	client := &http.Client{}
	resp, err := client.Do(request)
	if err != nil {
		//Warning.Println(err)
		printer.Active = false
		return err
	}
	defer resp.Body.Close()

	Manager.Println(resp.StatusCode)

	if resp.StatusCode != 204 {
		return errors.New("Wrong status code: " + resp.Status)
	}

	CurrentCommand = ""

	return nil
}
