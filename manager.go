package main

import "runtime"

func startManaging() {
	Manager.Println("Starting manager")
	for {
		runtime.Gosched()
		for _, printer := range printers {
			//Manager.Printf("Selecting Printer: %s", printer.Name)

			if printer.Status == PIdle && printer.Active {
				//Manager.Printf("Printer Available")

				for _, nametag := range nametags {
					//Manager.Printf("Selecting Nametag: %s", nametag.Name)
					if nametag.Status == PIdle {
						Manager.Printf("Processing Nametag: %s", nametag.Name)

						nametag.PrinterID = printer.ID
						printer.NametagID = nametag.ID

						nametag.Status = NRendering
						printer.Status = PRendering
						printer.renderNametag(nametag.ID)

						if printer.Status == PIdle {
							printer.NametagID = 0
							continue
						}
						if nametag.Status == NIdle {
							nametag.PrinterID = 0
							continue
						}

						nametag.Status = NSlicing
						printer.Status = PSlicing
						printer.sliceNametag(nametag.ID)

						if printer.Status == PIdle {
							printer.NametagID = 0
							continue
						}
						if nametag.Status == NIdle {
							nametag.PrinterID = 0
							continue
						}

						nametag.Status = NUploading
						printer.Status = PUploading
						printer.uploadNametag(nametag.ID)

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
