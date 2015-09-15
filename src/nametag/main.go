package main

import (
	"encoding/json"
	"html"
	"html/template"
	"io"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

var nametags []Nametag
var printers []Printer

var root string

var (
	// Debug logging
	Debug *log.Logger

	// Info logging
	Info *log.Logger

	// Warning logging
	Warning *log.Logger

	// Error logging
	Error *log.Logger

	// Fatal logging
	Fatal *log.Logger
)

func handleRoot(w http.ResponseWriter, r *http.Request) {
	Info.Println("Serving: " + r.Host + html.EscapeString(r.URL.Path))

	url := r.URL.Path

	if url == "/" {
		t, terr := template.ParseFiles(root + "/assets/templates/index.html")
		if terr != nil {
			//Error.Println("Error Finding Files:")
			//Error.Println(terr)
		}
		t.Execute(w, nametags)
	} else {
		if strings.HasSuffix(url, ".html") {
			//Info.Println()
			t, terr := template.ParseFiles(root + "/assets/templates" + url)
			if terr != nil {
				//Error.Println("Error Finding Files:")
				//Error.Println(terr)
			}
			t.Execute(w, nametags)
		} else {
			f, e := ioutil.ReadFile(root + url)
			if e != nil {
				w.WriteHeader(http.StatusNotFound)
				return
			}

			if len(f) <= 0 {
				w.WriteHeader(http.StatusConflict)
				return
			}

			if strings.HasSuffix(url, ".png") {
				w.Header().Set("Content-Type", "image/png; charset=utf-8")
			}

			w.Write(f)
		}
	}
}

func handlePrinters(w http.ResponseWriter, r *http.Request) {

	jsonPrinters, jerr := json.Marshal(printers)

	if jerr != nil {
		Error.Println(jerr)
	}

	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.Write(jsonPrinters)

}

func handleUpdatePrinter(w http.ResponseWriter, r *http.Request) {
	Info.Println("Printer Add Requested")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving addition:")
		Error.Println(rerr)
		w.WriteHeader(http.StatusInternalServerError)
	} else {

		var dat map[string]interface{}

		jerr := json.Unmarshal(body, &dat)

		if jerr != nil {
			Error.Println("Error parsing JSON:", jerr)
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		var printer Printer

		if id, ok := dat["id"]; ok {
			for _, p := range printers {
				if p.ID == int(id.(float64)) {
					printer = p
					break
				}
			}
		} else {
			printer.generateID()
		}

		if name, ok := dat["name"]; ok {
			printer.Name = name.(string)
		}

		if active, ok := dat["active"]; ok {
			printer.Active = active.(bool)
		}

		if printing, ok := dat["name"]; ok {
			printer.Printing = printing.(bool)
		}

		if nametag, ok := dat["nametag"]; ok {
			printer.Nametag = int(nametag.(float64))
		}

		if ip, ok := dat["name"]; ok {
			printer.IP = ip.(string)
		}

		if apikey, ok := dat["name"]; ok {
			printer.APIKey = apikey.(string)
		}

		if slicer, ok := dat["name"]; ok {
			printer.SlicerConf = slicer.(string)
		}

		append(printers, printer)

	}

}

func handleNametags(w http.ResponseWriter, r *http.Request) {

	jsonNametags, jerr := json.Marshal(nametags)

	if jerr != nil {
		Error.Println(jerr)
	}

	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.Write(jsonNametags)

}

func handleDeleteNametag(w http.ResponseWriter, r *http.Request) {
	Info.Println("Delete Requested")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving delete:")
		Error.Println(rerr)
		w.WriteHeader(http.StatusInternalServerError)
	} else {

		var dat map[string]interface{}

		jerr := json.Unmarshal(body, &dat)

		if jerr != nil {
			Error.Println("Error parsing JSON:", jerr)
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		if id, ok := dat["id"]; ok && id != "" {
			Info.Println(id)

			//var nametag Nametag
			var index int

			for i, n := range nametags {
				//Info.Println(i)
				//Info.Println(n.ID)
				//Info.Println(id)
				if n.ID == int(id.(float64)) {
					//Info.Println("Found Nametag to delete: " + string(i))
					index = i
					break
				}
			}
			Info.Println(nametags)
			nametags = append(nametags[:index], nametags[index+1:]...)
			Info.Println(nametags)

			return
		}
		w.WriteHeader(http.StatusNotAcceptable)
	}

}

func handlePreview(w http.ResponseWriter, r *http.Request) {
	Info.Println("Preview Requested")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving preview:")
		Error.Println(rerr)
		w.WriteHeader(http.StatusInternalServerError)
	} else {

		var dat map[string]interface{}

		jerr := json.Unmarshal(body, &dat)

		if jerr != nil {
			Error.Println("Error parsing JSON:", jerr)
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		if name, ok := dat["name"]; ok && name != "" {
			Info.Println(name)
			//w.WriteHeader(http.StatusOK)
			io.WriteString(w, "/assets/images/"+name.(string)+".png")

			go previewNametag(name.(string))

			return
		}
		w.WriteHeader(http.StatusNotAcceptable)
	}
}

func handleSubmit(w http.ResponseWriter, r *http.Request) {
	Info.Println("Nametag Submitted")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving submission:")
		Error.Println(rerr)
		w.WriteHeader(http.StatusInternalServerError)
	} else {

		var dat map[string]interface{}

		jerr := json.Unmarshal(body, &dat)

		if jerr != nil {
			Error.Println("Error parsing JSON:", jerr)
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		if name, ok := dat["name"]; ok && name != "" {
			Info.Println(name)
			var nametag Nametag
			nametag.generateID()
			nametag.Name = name.(string)
			nametags = append(nametags, nametag)
			Info.Println(strconv.Itoa(len(nametags)) + " Nametag(s) in queue")
			w.WriteHeader(http.StatusOK)
			return
		}
		w.WriteHeader(http.StatusNotAcceptable)
	}
}

func setupLoggers(
	debugHandle io.Writer,
	infoHandle io.Writer,
	warningHandle io.Writer,
	errorHandle io.Writer,
	fatalHandle io.Writer) {

	Debug = log.New(debugHandle,
		"DEBUG: ",
		log.Ldate|log.Ltime|log.Lshortfile)

	Info = log.New(infoHandle,
		"INFO: ",
		log.Ldate|log.Ltime|log.Lshortfile)

	Warning = log.New(warningHandle,
		"WARNING: ",
		log.Ldate|log.Ltime|log.Lshortfile)

	Error = log.New(errorHandle,
		"ERROR: ",
		log.Ldate|log.Ltime|log.Lshortfile)

	Fatal = log.New(fatalHandle,
		"FATAL: ",
		log.Ldate|log.Ltime|log.Lshortfile)
}

func main() {

	setupLoggers(ioutil.Discard, os.Stdout, os.Stdout, os.Stderr, os.Stderr)

	Info.Println("Starting Nametag App...")

	if len(os.Args) > 1 {
		root = os.Args[1]
	} else {
		root, _ = filepath.Abs(filepath.Dir(os.Args[0]))
	}

	Info.Printf("Using %s as root\n", root)

	http.HandleFunc("/", handleRoot)
	http.HandleFunc("/nametags", handleNametags)
	http.HandleFunc("/nametags/delete", handleDeleteNametag)
	http.HandleFunc("/printers", handlePrinters)
	http.HandleFunc("/printers/update", handleUpdatePrinter)
	http.HandleFunc("/preview", handlePreview)
	http.HandleFunc("/submit", handleSubmit)

	http.ListenAndServe(":8080", nil)
}
