# Nametag-Auto-Printing
The code to run on a laptop to automatically collect a customer's name and generate gcode to print.

So far, I have previewing and submitting working, as well as the nametag queue list on the manager page. The printer list should work, but there is not yet a way to add a printer. You can also delete nametags from the queue. Everything is done through AJAX, there is no server side editing of the HTML, although it is implemented as a template, so it could if it needed to. The UI has no styling yet, as I want to get stuff working before I worry about how it looks. Every nametag and printer has a 3 digit id that is randomly generated when a nametag or printer is created. It should never repeat ids, but I have not tested it 1000 times. The binary takes as an argument the root of the files to serve. If none is provided, it defaults to the location of the binary. The JavaScript updates the printer and nametag lists every second using AJAX and only processes the JSON if the JSON has changed since the last check. The preview js will send the preview request, then check every second for the image, and checks if the image has a size > 0.


#Printer Properties:
<pre>
type Printer struct {
    ID         int    // Unique ID of the printer
    Name       string // Readable name for the printer
    Nametag    int    // Nametag ID that is currently printing
    IP         string // IP for the printer
    APIKey     string // API Key to use for the printer
    SlicerConf string // Slicer config path
    Active     bool   // Active or not
    Printing   bool   // Printing or not
    Slicing    bool   // Slicing or not
    Uploading  bool   // Uploading or not
}
</pre>
#Nametag Properties:
<pre>
type Nametag struct {
    ID        int    // Unique ID of the nametag
    Name      string // Name on the nametag
    StlPath   string // Path to the stl
    GcodePath string // Path to the gcode
    ImagePath string // Path to the image
    Rendered  bool   // Exported from Scad or not
    Sliced    bool   // Sliced or not
    Uploaded  bool   // Uploaded or not
    Printed   bool   // Printed or not
    PrinterID int64  // Printer id that nametag will print on
}
</pre>
#Summary of the api so far:

GET / - Gets the name entering page, also serves resources such as images and js. Go to /manager.html for the manager.
    Returns the resource specified if available, 404 if not found. If image request has size 0 (therefore not yet done generating), returns a 409 CONFLICT

POST /preview - Previews the nametag
    <pre>
    {
        "name": "Name on the nametag"
    }
    </pre>
    Returns that the image will be at once generated
    InternalServerError: Error receiving request
    BadRequest: Bad JSON
    OK: Everything good
    StatusNotAcceptable: No name key or name is empty
    
POST /submit - Submits a nametag
    <pre>
    {
        "name": "Name on the nametag"
    }
    </pre>
    Returns nothing
    InternalServerError: Error receiving request
    BadRequest: Bad JSON
    OK: Everything good
    StatusNotAcceptable: No name key or name is empty

GET /nametags - Gets the nametag queue
    Returns JSON list of nametags in queue and their properties
  

POST /nametags/delete - Deletes a nametag
    <pre>
    {
        "id": "id of on the nametag"
    }
    </pre>
    Returns nothing
    InternalServerError: Error receiving request
    BadRequest: Bad JSON
    OK: Everything good
    StatusNotAcceptable: No id key or id is empty


GET /printers - Gets the printers
    Returns JSON list of printers and their properties

POST /printers/update - Update a printer
    A JSON form of the printer and its properties
    If an ID is suplied, edit that printer, otherwise add the printer and generate an ID
    NOT YET FULLY IMPLEMENTED
