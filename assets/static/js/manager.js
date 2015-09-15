
var nametagDiv = document.getElementById("nametags")
var printerDiv = document.getElementById("printers")
var nametagTable = document.getElementById("nametagTable")
var printerTable = document.getElementById("printerTable")

var nametags
var oldNametagJson = ""
var printers
var oldPrinterJson = ""

window.setInterval("update()", 1000);

showNametags()

function update(){
  //console.log("Updating")
  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    if(http.readyState == 4){
      processNametags(http.responseText)
    }
  }
  http.open("GET", "nametags", true)
  http.send();

  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    if(http.readyState == 4){
      processPrinters(http.responseText)
    }
  }
  http.open("GET", "printers", true)
  http.send();

}

function processPrinters(json){
  if(json != oldPrinterJson && (json != "null" || json != "")){
    console.log(json)
    printers = JSON.parse(json)

    for(i = 1; i < printerTable.rows.length; i++){
      printerTable.deleteRow(i)
    }

    for(i = 0; i < printers.length; i++){
      if(printerTable.rows.length > i+1){
        printerTable.deleteRow(i+1)
      }
      var row = printerTable.insertRow(i+1)


      var idCell = row.insertCell(0)
      idCell.innerHTML = nametags[i].ID

      var nameCell = row.insertCell(1)
      nameCell.innerHTML = nametags[i].Name

      var activeCell = row.insertCell(2)
      activeCell.innerHTML = nametags[i].Active

      var printingCell = row.insertCell(3)
      printingCell.innerHTML = nametags[i].Printing

      var NametagCell = row.insertCell(4)
      nametagCell.innerHTML = nametags[i].Nametag

      var ipCell = row.insertCell(5)
      ipCell.innerHTML = nametags[i].IP

      var apiCell = row.insertCell(6)
      apiCell.innerHTML = nametags[i].APIKey

      var slicerCell = row.insertCell(7)
      slicerCell.innerHTML = nametags[i].SlicerConf

      var deleteCell = row.insertCell(8)
      deleteCell.innerHTML = "<button onclick=\"deleteNametag(" + nametags[i].ID + ")\">Delete</button>"

    }
    oldPrinterJson = json
  }
}

function addPrinter(){

}

function processNametags(json){
  if(json != oldNametagJson && (json != "null" || json != "")){
    console.log(json)
    nametags = JSON.parse(json)

    for(i = 1; i < nametagTable.rows.length; i++){
      nametagTable.deleteRow(i)
    }

    for(i = 0; i < nametags.length; i++){
      if(nametagTable.rows.length > i+1){
        nametagTable.deleteRow(i+1)
      }
      var row = nametagTable.insertRow(i+1)


      var idCell = row.insertCell(0)
      idCell.innerHTML = nametags[i].ID

      var nameCell = row.insertCell(1)
      nameCell.innerHTML = nametags[i].Name

      var nameCell = row.insertCell(2)
      nameCell.innerHTML = nametags[i].Rendered

      var nameCell = row.insertCell(3)
      nameCell.innerHTML = nametags[i].Sliced

      var nameCell = row.insertCell(4)
      nameCell.innerHTML = nametags[i].Uploaded

      var nameCell = row.insertCell(5)
      nameCell.innerHTML = nametags[i].Printed

      var nameCell = row.insertCell(6)
      nameCell.innerHTML = nametags[i].Printer

      var nameCell = row.insertCell(7)
      nameCell.innerHTML = "<button onclick=\"deleteNametag(" + nametags[i].ID + ")\">Delete</button>"

    }
    oldNametagJson = json
  }
}

function deleteNametag(id){
  console.log("Submitting")
  var nametag = {
    "id": ""
  }
  nametag.id = id
  //console.log(nametag.name)
  var body = JSON.stringify(nametag)
  console.log(body)
  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    console.log("DONE DELETING")
    //console.log(http.responseText)
  }
  http.open("POST", "nametags/delete", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
}

function showNametags(){
  nametagDiv.style.display = "block"
  printerDiv.style.display = "none"
}

function showPrinters(){
  nametagDiv.style.display = "none"
  printerDiv.style.display = "block"
}
