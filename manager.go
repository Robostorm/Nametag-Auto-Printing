package main

import (
	"runtime"
	"time"
)

// Managing controls whether the manager is running
var Managing bool

func startManaging() {

	ticker := time.NewTicker(time.Millisecond * 250)

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
					if nametag != nil && nametag.Status == PIdle && printer.Active && (printer.Status == PIdle || printer.Status == PErrored) {

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

						if printer.Status == PIdle {
							printer.NametagID = 0
							continue
						}

						if nametag.Status == NIdle {
							nametag.PrinterID = 0
							continue
						}

						/******************************************************************************
						███████ ██      ██  ██████ ███████
						██      ██      ██ ██      ██
						███████ ██      ██ ██      █████
						     ██ ██      ██ ██      ██
						███████ ███████ ██  ██████ ███████
						******************************************************************************/

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

						if printer.Status == PIdle {
							printer.NametagID = 0
							continue
						}

						if nametag.Status == NIdle {
							nametag.PrinterID = 0
							continue
						}

						/******************************************************************************
						██    ██ ██████  ██       ██████   █████  ██████
						██    ██ ██   ██ ██      ██    ██ ██   ██ ██   ██
						██    ██ ██████  ██      ██    ██ ███████ ██   ██
						██    ██ ██      ██      ██    ██ ██   ██ ██   ██
						 ██████  ██      ███████  ██████  ██   ██ ██████
						******************************************************************************/

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

//
// func processNametag(id int) {
// 	_, nametag := findNametag(id)
// 	_, printer := findPrinter(nametag.PrinterID)
// 	nametag.Processing = true
//
// 	Manager.Printf("Processing Nametag %d for Printer %d\n", nametag.ID, printer.ID)
//
// 	for {
// 		switch nametag.Status {
// 		case NIdle:
// 			nametag.Status = NRendering
// 		case NRendering:
// 			if !nametag.exists() {
// 				Manager.Printf("Nametag %d dissapeared! Returning\n", nametag.ID)
// 				printer.Status = PIdle
// 				return
// 			}
// 			printer.renderNametag(id)
// 			nametag.Status = NSlicing
// 		case NSlicing:
// 			if !nametag.exists() {
// 				Manager.Printf("Nametag %d dissapeared! Returning\n", nametag.ID)
// 				printer.Status = PIdle
// 				return
// 			}
// 			printer.sliceNametag(id)
// 			nametag.Status = NUploading
// 		case NUploading:
// 			if !nametag.exists() {
// 				Manager.Printf("Nametag %d dissapeared! Returning\n", nametag.ID)
// 				printer.Status = PIdle
// 				return
// 			}
// 			printer.uploadNametag(id)
// 			nametag.Status = NPrinting
// 			printer.Status = PPrinting
// 			Manager.Printf("Printing %d\n", nametag.ID)
// 		case NPrinting:
// 			if !nametag.exists() {
// 				Manager.Printf("Nametag %d dissapeared! Returning\n", nametag.ID)
// 				printer.Status = PIdle
// 				return
// 			}
// 		case NDone:
// 			Manager.Printf("Done %d\n", nametag.ID)
// 			printer.Status = PIdle
// 			printer.Available = true
// 			printer.NametagID = 0
// 			_ = "breakpoint"
// 			//nametags = append(nametags[:index], nametags[index+1:]...)
// 			return
// 		}
// 	}
//}

func findNametag(id int) (int, *Nametag) {
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
