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
	"sync"
)

var nametags []*Nametag
var nametagsMux sync.Mutex
var printers []*Printer

var client http.Client

var (
	// Debug logging
	Debug *log.Logger

	// Main logging
	Main *log.Logger

	// Manager logging
	Manager *log.Logger

	// Server logging
	Server *log.Logger

	// Warning logging
	Warning *log.Logger

	// Error logging
	Error *log.Logger

	// Fatal logging
	Fatal *log.Logger
)

var connections = 0

// Column should be used to to define a list of columns for the interface for printers and nametags
type Column struct {
	Name     string `json:"name"`     // The name of the field in the struct
	Label    string `json:"label"`    // The name of the header of the column
	Type     string `json:"cell"`     // The type of the data
	Editable bool   `json:"editable"` // If the column is editable
}

func handleRoot(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Serving: " + r.Host + html.EscapeString(r.URL.Path))

	url := r.URL.Path

	if url == "/" {
		t, terr := template.ParseFiles(Root + "/assets/templates/index.html")
		if terr != nil {
			Error.Println("Error Finding Files:")
			Error.Println(terr)
		}
		t.Execute(w, Root)
	} else {
		if strings.HasSuffix(url, ".html") {
			t, terr := template.ParseFiles(Root + "/assets/templates" + url)
			if terr != nil {
				//Error.Println("Error Finding Files:")
				//Error.Println(terr)
			}
			t.Execute(w, nil)
		} else {
			f, e := ioutil.ReadFile(Root + url)
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
			} else if strings.HasSuffix(url, ".css") {
				w.Header().Set("Content-Type", "text/css; charset=utf-8")
			}

			w.Write(f)
		}
	}
}

func handlePrinters(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)

	jsonPrinters, jerr := json.Marshal(printers)

	if jerr != nil {
		Error.Println(jerr)
	}

	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.Write(jsonPrinters)
}

func handleUpdatePrinter(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Printer Update Requested")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving addition:")
		Error.Println(rerr)
		w.WriteHeader(http.StatusInternalServerError)
	} else {

		var printer Printer

		jerr := json.Unmarshal(body, &printer)

		if jerr != nil {
			Error.Println("Error parsing JSON:", jerr)
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		if printer.ID == 0 {
			Server.Println("Generating ID")
			printer.generateID()
			printers = append(printers, &printer)
		} else {
			_, newPrinter := findPrinter(printer.ID)
			*newPrinter = printer
		}

		savePrinters()

	}
}

func handleDeletePrinter(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Delete Printer Requested")
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
			p, printer := findPrinter(int(id.(float64)))
			if printer != nil {
				printers = append(printers[:p], printers[p+1:]...)
				_, nametag := findNametag(printer.NametagID)
				if nametag != nil {
					nametag.Status = NIdle
					nametag.PrinterID = 0
				}
				savePrinters()
			} else {
				Warning.Printf("Tried to delete nonexistent printer at index %d\n", p)
			}
			return
		}
		w.WriteHeader(http.StatusNotAcceptable)
	}
}

func handleDonePrinter(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Done Printer Requested")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving done:")
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
			p, printer := findPrinter(int(id.(float64)))
			if printer != nil {
				Server.Println(printer.NametagID)
				n, nametag := findNametag(printer.NametagID)
				if nametag != nil {
					nametagsMux.Lock()
					nametags = append(nametags[:n], nametags[n+1:]...)
					nametagsMux.Unlock()
					printer.Status = PIdle
					printer.NametagID = 0
					savePrinters()
					saveNametags()
				} else {
					Warning.Printf("Tried to delete nonexistent nametag at index %d\n", n)
				}
			} else {
				Warning.Printf("Tried to set nonexistent printer at index %d to done\n", p)
			}
			return
		}
		Warning.Println("Done requested, but no ID found!")
		w.WriteHeader(http.StatusNotAcceptable)
	}
}

func handleAbortPrinter(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Abort Printer Requested")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving done:")
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
			p, printer := findPrinter(int(id.(float64)))
			if printer != nil {
				_, nametag := findNametag(printer.NametagID)
				if nametag != nil {
					nametag.Status = NIdle
					nametag.PrinterID = 0
					saveNametags()
				}
				printer.Status = PIdle
				printer.NametagID = 0
				printer.Active = false
				savePrinters()
			} else {
				Warning.Printf("Tried to abort nonexistent printer at index %d\n", p)
			}
			return
		}
		w.WriteHeader(http.StatusNotAcceptable)
	}
}

func handlePrinterColumns(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)

	jsonPrinterColumns, jerr := json.Marshal(PrinterColumns)

	if jerr != nil {
		Error.Println(jerr)
	}

	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.Write(jsonPrinterColumns)
}

func handleNametags(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)

	nametagsMux.Lock()
	jsonNametags, jerr := json.Marshal(nametags)
	nametagsMux.Unlock()

	if jerr != nil {
		Error.Println(jerr)
	}

	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.Write(jsonNametags)
}

func handleUpdateNametag(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Nametag Update Requested")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving addition:")
		Error.Println(rerr)
		w.WriteHeader(http.StatusInternalServerError)
	} else {

		var nametag Nametag

		jerr := json.Unmarshal(body, &nametag)

		if jerr != nil {
			Error.Println("Error parsing JSON:", jerr)
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		if nametag.ID == 0 {
			Server.Println("Generating ID")
			nametag.generateID()
			nametag.Status = NIdle
			nametagsMux.Lock()
			nametags = append(nametags, &nametag)
			nametagsMux.Unlock()
		} else {
			Server.Println("Searching for id")
			_, newNametag := findNametag(nametag.ID)
			*newNametag = nametag
		}

		saveNametags()

	}
}

func handleDeleteNametag(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Delete Requested")
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
			n, nametag := findNametag(int(id.(float64)))
			if nametag != nil {
				nametagsMux.Lock()
				nametags = append(nametags[:n], nametags[n+1:]...)
				nametagsMux.Unlock()
				_, printer := findPrinter(nametag.PrinterID)
				if printer != nil {
					printer.Status = PIdle
					printer.NametagID = 0
				}
				saveNametags()
			} else {
				Warning.Printf("Tried to delete nonexistent nametag %s at index %d\n", nametag.Name, n)
			}
			return
		}
		w.WriteHeader(http.StatusNotAcceptable)
	}

}

func handleDoneNametag(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Done Requested")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving done:")
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
			n, nametag := findNametag(int(id.(float64)))
			if nametag != nil {
				Server.Println(nametag.PrinterID)
				p, printer := findPrinter(nametag.PrinterID)
				if printer != nil {
					nametagsMux.Lock()
					nametags = append(nametags[:n], nametags[n+1:]...)
					nametagsMux.Unlock()
					printer.Status = PIdle
					savePrinters()
					saveNametags()
				} else {
					Warning.Printf("Tried to set nonexistent printer at index %d to done\n", p)
				}
			} else {
				Warning.Printf("Tried to delete nonexistent nametag at index %d to done\n", n)
			}
			return
		}

		// if id, ok := dat["id"]; ok && id != "" {
		//
		// 	_, nametag := findNametag(int(id.(float64)))
		// 	nametag.Status = NDone
		// 	//saveNametags()
		//
		// 	return
		// }
		w.WriteHeader(http.StatusNotAcceptable)
	}
}

func handleNametagColumns(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)

	jsonNametagColumns, jerr := json.Marshal(NametagColumns)

	if jerr != nil {
		Error.Println(jerr)
	}

	w.Header().Set("Content-Type", "application/json; charset=utf-8")
	w.Write(jsonNametagColumns)
}

func handlePreview(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Preview Requested")
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
			Server.Println(name)
			//w.WriteHeader(http.StatusOK)
			io.WriteString(w, "/assets/images/nametags/"+name.(string)+".png")

			go previewNametag(name.(string))

			return
		}
		w.WriteHeader(http.StatusNotAcceptable)
	}
}

func handleSubmit(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println(connections)
	Server.Println("Nametag Submitted")
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
			Server.Println(name)
			var nametag Nametag
			nametag.generateID()
			nametag.Name = name.(string)

			if printerID, ok := dat["printer-id"]; ok && printerID != "" {
				id, perr := strconv.ParseInt(printerID.(string), 10, 0)
				if perr != nil {
					Warning.Println(perr)
				} else {
					nametag.PrinterID = int(id)
				}
			}

			if color, ok := dat["color"]; ok && color != "" {
				nametag.Comments = color.(string)
			}

			nametag.Status = NIdle
			nametagsMux.Lock()
			nametags = append(nametags, &nametag)
			Server.Println(strconv.Itoa(len(nametags)) + " Nametag(s) in queue")
			saveNametags()
			w.WriteHeader(http.StatusOK)
			return
		}
		w.WriteHeader(http.StatusNotAcceptable)
	}
}

func handleManagingState(w http.ResponseWriter, r *http.Request) {
	connections++
	//Server.Println("Manager State Change Request")
	body, rerr := ioutil.ReadAll(r.Body)
	if rerr != nil {
		Error.Println("Error reciving submission:")
		Error.Println(rerr)
		w.WriteHeader(http.StatusInternalServerError)
	} else {

		var dat map[string]interface{}

		jerr := json.Unmarshal(body, &dat)

		if jerr == nil {

			if managing, ok := dat["managing"]; ok {
				if managing == true && !Managing {
					Server.Println("Starting Manager")
					Managing = true
				} else if managing == false && Managing {
					Server.Println("Stopping Manager")
					Managing = false
				}
			}
		}
		manager := struct {
			Managing       bool   `json:"managing"`
			CurrentCommand string `json:"currentCommand"`
		}{
			Managing,
			CurrentCommand,
		}

		json, jerr := json.Marshal(manager)

		if jerr != nil {
			Error.Println(jerr)
		}

		w.Header().Set("Content-Type", "application/json; charset=utf-8")
		w.Write(json)
	}
}

func setupLoggers(
	debugHandle io.Writer,
	mainHandle io.Writer,
	managerHandle io.Writer,
	serverHandle io.Writer,
	warningHandle io.Writer,
	errorHandle io.Writer,
	fatalHandle io.Writer) {

	Debug = log.New(debugHandle,
		"DEBUG: ",
		log.Ldate|log.Ltime|log.Lshortfile)

	Main = log.New(mainHandle,
		"MAIN: ",
		log.Ldate|log.Ltime|log.Lshortfile)

	Manager = log.New(mainHandle,
		"MANAGER: ",
		log.Ldate|log.Ltime|log.Lshortfile)

	Server = log.New(mainHandle,
		"SERVER: ",
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

	setupLoggers(os.Stdout, os.Stdout, os.Stdout, os.Stdout, os.Stdout, os.Stderr, os.Stderr)

	Main.Println("Starting Nametag App...")

	if len(os.Args) > 1 {
		Root = os.Args[1]
	} else {
		Root, _ = filepath.Abs(filepath.Dir(os.Args[0]))
	}

	if !strings.HasSuffix(Root, "/") {
		Root = Root + "/"
	}

	Main.Printf("Using %s as Root\n", Root)

	loadMain()
	loadNametags()
	loadPrinters()

	Main.Println("Loaded Configs")

	Managing = false

	go startManaging()

	Main.Println("Started Manager")

	http.HandleFunc("/", handleRoot)
	http.HandleFunc("/nametags", handleNametags)
	http.HandleFunc("/nametags/update", handleUpdateNametag)
	http.HandleFunc("/nametags/delete", handleDeleteNametag)
	http.HandleFunc("/nametags/done", handleDoneNametag)
	http.HandleFunc("/nametags/columns", handleNametagColumns)
	http.HandleFunc("/printers", handlePrinters)
	http.HandleFunc("/printers/update", handleUpdatePrinter)
	http.HandleFunc("/printers/delete", handleDeletePrinter)
	http.HandleFunc("/printers/abort", handleAbortPrinter)
	http.HandleFunc("/printers/done", handleDonePrinter)
	http.HandleFunc("/printers/columns", handlePrinterColumns)
	http.HandleFunc("/manager/state", handleManagingState)
	http.HandleFunc("/preview", handlePreview)
	http.HandleFunc("/submit", handleSubmit)

	Main.Println("Serving on port 8080")

	err := http.ListenAndServe(":8080", nil)

	if err != nil {
		Main.Fatal(err)
	}

	Main.Println("Done Serving")
}
