package main

// Printer for printing nametags
type Printer struct {
	ID         int    `json:"ID"`            // Unique ID of the printer
	Name       string `json:"Name"`          // Readable name for the printer
	Status     string `json:"_Status"`       // Status of the printer
	Active     bool   `json:"Active"`        // Active or not
	Available  bool   `json:"Available"`     // Available or not
	NametagID  int    `json:"Nametag ID"`    // Nametag ID that is currently printing
	IP         string `json:"IP"`            // IP for the printer
	APIKey     string `json:"API Key"`       // API Key to use for the printer
	SlicerConf string `json:"Slicer Config"` // Slicer config path
}

func (printer *Printer) generateID() int {

	for id := 10; ; id++ {
		Info.Printf("Trying ID: %d", id)
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

	Info.Println(printer.ID)
	return printer.ID
}
