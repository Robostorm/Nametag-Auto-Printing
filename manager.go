package main

import (
	"runtime"
	"time"
)

// Managing controls whether the manager is running
var Managing bool

// CurrentCommand is the command that is currently running
var CurrentCommand string

func startManaging() {

	//ticker := time.NewTicker(time.Millisecond * 250)
	ticker := time.NewTicker(time.Second)

	for {
		select {
		case <-ticker.C:
			if Managing {
				manage()
			}
		}
	}
}

func manage() {
	runtime.Gosched()

	if len(nametags) > 0 {
		for _, printer := range printers {
			//Manager.Printf("Selecting Printer: %s", printer.Name)

			if printer != nil && printer.Active && (printer.Status == PIdle || printer.Status == PErrored) {
				//Manager.Printf("Printer Available")

				for _, nametag := range nametags {
					//Manager.Printf("Selecting Nametag: %s", nametag.Name)
					if nametag != nil && nametag.Status == NIdle && printer.Active && (printer.Status == PIdle || printer.Status == PErrored) {

						if nametag.PrinterID != 0 && nametag.PrinterID != printer.ID {
							continue
						}

						Manager.Printf("Processing Nametag: %s", nametag.Name)

						nametag.PrinterID = printer.ID
						printer.NametagID = nametag.ID

						/******************************************************************************
						██████  ███████ ███    ██ ██████  ███████ ██████
						██   ██ ██      ████   ██ ██   ██ ██      ██   ██
						██████  █████   ██ ██  ██ ██   ██ █████   ██████
						██   ██ ██      ██  ██ ██ ██   ██ ██      ██   ██
						██   ██ ███████ ██   ████ ██████  ███████ ██   ██
						******************************************************************************/

						oldNametag := *nametag

						nametag.Status = NRendering
						printer.Status = PRendering
						err := printer.renderNametag(nametag.ID)

						if err != nil {
							Warning.Println(err)
							Debug.Println(printer.Name + " is not active")
							Warning.Println(printer.Name + " errored rendering " + nametag.Name)
							printer.Active = false
							printer.Status = PErrored
							nametag.Status = NIdle
							continue
						}

						if oldNametag.Name != nametag.Name || oldNametag.PrinterID != nametag.PrinterID {
							nametag.Status = NIdle
							printer.Status = PIdle
						}

						if printer.Status == PIdle {
							printer.NametagID = 0
							break
						}

						if nametag.Status == NIdle {
							nametag.PrinterID = 0
							break
						}

						/******************************************************************************
						███████ ██      ██  ██████ ███████
						██      ██      ██ ██      ██
						███████ ██      ██ ██      █████
						     ██ ██      ██ ██      ██
						███████ ███████ ██  ██████ ███████
						******************************************************************************/

						oldNametag = *nametag

						nametag.Status = NSlicing
						printer.Status = PSlicing
						err = printer.sliceNametag(nametag.ID)

						if err != nil {
							Warning.Println(err)
							Debug.Println(printer.Name + " is not active")
							Warning.Println(printer.Name + " errored slicing " + nametag.Name)
							printer.Active = false
							printer.Status = PErrored
							nametag.Status = NIdle
							continue
						}

						if oldNametag.Name != nametag.Name || oldNametag.PrinterID != nametag.PrinterID {
							nametag.Status = NIdle
							printer.Status = PIdle
						}

						if printer.Status == PIdle {
							printer.NametagID = 0
							break
						}

						if nametag.Status == NIdle {
							nametag.PrinterID = 0
							break
						}

						/******************************************************************************
						██    ██ ██████  ██       ██████   █████  ██████
						██    ██ ██   ██ ██      ██    ██ ██   ██ ██   ██
						██    ██ ██████  ██      ██    ██ ███████ ██   ██
						██    ██ ██      ██      ██    ██ ██   ██ ██   ██
						 ██████  ██      ███████  ██████  ██   ██ ██████
						******************************************************************************/

						oldNametag = *nametag

						nametag.Status = NUploading
						printer.Status = PUploading
						err = printer.uploadNametag(nametag.ID)

						if err != nil {
							Warning.Println(err)
							Debug.Println(printer.Name + " is not active")
							Warning.Println(printer.Name + " errored uploading " + nametag.Name)
							printer.Active = false
							printer.Status = PErrored
							nametag.Status = NIdle
							continue
						}

						err = printer.selectNametag(nametag.ID)
						if err != nil {
							Warning.Println(err)
							Debug.Println(printer.Name + " is not active")
							Warning.Println(printer.Name + " errored selecting " + nametag.Name)
							printer.Active = false
							printer.Status = PErrored
							nametag.Status = NIdle
							continue
						}

						err = printer.print()
						if err != nil {
							Warning.Println(err)
							Debug.Println(printer.Name + " is not active")
							Warning.Println(printer.Name + " errored printing " + nametag.Name)
							printer.Active = false
							printer.Status = PErrored
							nametag.Status = NIdle
							continue
						}

						if oldNametag.Name != nametag.Name || oldNametag.PrinterID != nametag.PrinterID {
							nametag.Status = NIdle
							printer.Status = PIdle
						}

						if printer.Status == PIdle {
							printer.NametagID = 0
							continue
						}

						if nametag.Status == NIdle {
							nametag.PrinterID = 0
							continue
						}

						printer.Status = PPrinting
						nametag.Status = NPrinting

						break
					}
				}
			}
		}
	} else {
		CurrentID = 0
	}
}

func findNametag(id int) (int, *Nametag) {
	nametagsMux.Lock()
	defer nametagsMux.Unlock()
	for i := range nametags {
		if nametags[i].ID == id {
			return i, nametags[i]
		}
	}
	return -1, nil
}

func findPrinter(id int) (int, *Printer) {
	for i := range printers {
		if printers[i].ID == id {
			return i, printers[i]
		}
	}
	return -1, nil
}
