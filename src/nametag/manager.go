package main

import "time"

func startManaging() {
	for {
		for i := range nametags {
			if nametags[i].Processing == false {
				if nametags[i].findPrinter() {
					nametags[i].process()
				} else {
					Info.Println("No Printers Available")
					break
				}
			}
		}

		for i := range printers {
			found := false
			//Info.Printf("Checking printer %d for nonexistent nametag of %d", printers[i].ID, printers[i].NametagID)
			for j := range nametags {
				//Info.Printf("Checking nametag %d", nametags[j].ID)
				if printers[i].NametagID == nametags[j].ID {
					//Info.Printf("Match!")
					found = true
					break
				}
			}
			if !found && printers[i].NametagID != 0 {
				//Info.Printf("Found nonexist nametag: %d. Deleting.", printers[i].NametagID)
				printers[i].NametagID = 0
				printers[i].Available = true
			}
		}

		for i := range nametags {
			found := false
			//Info.Printf("Checking printer %d for nonexistent nametag of %d", nametags[i].ID, nametags[i].PrinterID)
			for j := range printers {
				//Info.Printf("Checking nametag %d", printers[j].ID)
				if nametags[i].PrinterID == printers[j].ID {
					//Info.Printf("Match!")
					found = true
					break
				}
			}
			if !found && nametags[i].PrinterID != 0 {
				//Info.Printf("Found nonexist nametag: %d. Deleting.", nametags[i].PrinterID)
				nametags[i].PrinterID = 0
				nametags[i].Processing = false
			}
		}

		//Info.Println("=========================")
		time.Sleep(time.Second)
	}
}
