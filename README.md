# Nametag-Auto-Printing

Webapp to take names and autonametically generate nametags with [Openscad](http://www.openscad.org/), slice it with [Slic3r](http://slic3r.org/), and upload and print it on an [Octoprint](http://octoprint.org/) enable 3d printer. Developed and used by the Robostorm Robotics club of Hunterdon County 4-H.

The name submission page will allow a name to previewed with Openscad, and submitted to the server. it will ensure that names are under ten characters.

The manager page is located at /manager.html and allows veiwing and editing of the nametags in queue and printers.

# Installation

First, install [Go](https://golang.org/). The server is written in go and must be compiled.

Make sure the your `GOHOME` environment variable is set correctly.

Get the source code by running `go get github.com/Robostorm/Nametag-Auto-Printing`. This will clone the source into go's `src` directory

Build and install the server into go's `bin` directory by running `go install github.com/Robostorm/Nametag-Auto-Printing`. Then run the resulting executable in `$GOHOME/bin`.

# REST API

`GET /`
----
Gets the name entering page, also serves resources such as images and js. Go to /manager.html for the manager.
Returns the resource specified if available, 404 NOT FOUND if the resource does not exist. If image requested is not yet generated, returns a 409 CONFLICT

`POST /preview`
---
Previews the nametag
```
{
"name": "Name on the nametag"
}
```
Returns that the image will be at once generated

Return Codes:
 - InternalServerError: Error receiving request
 - BadRequest: Bad JSON
 - OK: Everything good
 - StatusNotAcceptable: No name key or name is empty

`POST /submit`
---
Submits a nametag
```
{
"name": "Name on the nametag"
}
```
Returns nothing

Return Codes:
 - InternalServerError: Error receiving request
 - BadRequest: Bad JSON
 - OK: Everything good
 - StatusNotAcceptable: No name key or name is empty

`GET /nametags`
---
Gets the nametag queue
Returns JSON list of nametags in queue and their properties

`POST /nametags/update`
---
Update a nametag

A JSON form of the printer and its properties
If an ID is supplied, edit that nametag, otherwise add a new nametag with specifed fields and generate an ID

`POST /nametags/delete`
---
Deletes a nametag
```
{
"id": "id of the nametag"
}
```
Returns nothing

Return Codes:
 - InternalServerError: Error receiving request
 - BadRequest: Bad JSON
 - OK: Everything good
 - StatusNotAcceptable: No id key or id is empty

`POST /nametags/done`
---
The nametag is done printing. Sets the nametag's printer to Idle and deletes the nametag
```
{
"id": "id of the nametag"
}
```
Returns nothing

Return Codes:
  - InternalServerError: Error receiving request
  - BadRequest: Bad JSON
  - OK: Everything good
  - StatusNotAcceptable: No id key or id is empty

`GET /printers`
---
Gets the printers

Returns JSON list of printers and their properties

`POST /printers/update`
---
Update a printer

A JSON form of the printer and its properties
If an ID is supplied, edit that printer, otherwise add a new printer with specifed fields and generate an ID

`POST /printers/delete`
---
Deletes a printer
```
{
"id": "id of the printer"
}
```
Returns nothing

Return Codes:
 - InternalServerError: Error receiving request
 - BadRequest: Bad JSON
 - OK: Everything good
 - StatusNotAcceptable: No id key or id is empty

`POST /printers/done`
---
The printer is done printing. Sets the printer to Idle and deletes the printer's nametag
```
{
"id": "id of the printer"
}
```
Returns nothing

Return Codes:
  - InternalServerError: Error receiving request
  - BadRequest: Bad JSON
  - OK: Everything good
  - StatusNotAcceptable: No id key or id is empty

`POST /printers/abort`
---
Abort the current nametag. Sets printer and nametags to Idle.
```
{
"id": "id of the printer"
}
```
Returns nothing
Return Codes:
  - InternalServerError: Error receiving request
  - BadRequest: Bad JSON
  - OK: Everything good
  - StatusNotAcceptable: No id key or id is empty
