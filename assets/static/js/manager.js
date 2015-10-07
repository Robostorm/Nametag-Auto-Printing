
var nametagDiv = document.getElementById("nametags")
var printerDiv = document.getElementById("printers")
var nametagTable = document.getElementById("nametagTable")
var printerTable = document.getElementById("printerTable")

var nametags
var tmpNametag = {}
var oldNametagJson = "null"
var printers
var oldPrinterJson = "null"
var tmpPrinter = {}

updateNametags();
updatePrinters();

window.setInterval("update()", 1000)

showBoth()

function update(){
  //console.log("Updating")
  updateNametags();

  updatePrinters();

}

function updateNametags(){
  //console.log("Updating Nametags")
  nhttp = new XMLHttpRequest()
  nhttp.onreadystatechange = function (e){
    if(nhttp.readyState === 4){
      processNametags(nhttp.responseText)
    }
  }
  nhttp.open("GET", "nametags", true)
  nhttp.send();
}

function processNametags(json){
  console.log(json)
  if(json !== "[]" && json !== "null"){
  	if(json != oldNametagJson){
	    nametags = JSON.parse(json)

      var l = nametagTable.rows.length

	    for(i = 0; i < l; i++){
		    nametagTable.deleteRow(0)
	    }

	    var header = nametagTable.createTHead()
	    var hrow = header.insertRow(0)

	    for(var key in nametags[0]){
    		if(nametags[0].hasOwnProperty(key)){
    		  var cell = hrow.insertCell(-1)
          if(key.charAt(0) === "_"){
            key = key.substring(1);
          }
    		  cell.innerHTML = "<b>" + key + "</b>"
          tmpNametag[key] = ""
    		}
  	  }

      hrow.insertCell(-1)
      hrow.insertCell(-1)

      var footer = nametagTable.createTFoot()
	    var frow = footer.insertRow(-1)

      for(var key in nametags[0]){
    		if(nametags[0].hasOwnProperty(key)){
    		  var cell = frow.insertCell(-1)
          var data = nametags[0][key]
          if(key.charAt(0) === "_"){
            cell.innerHTML = "<span id=\"" + key + "Input\"></span>"
          }else if(key === "ID"){
            cell.innerHTML = "<span id=\"" + key + "Input\"></span>"
          }else{
            if(typeof data === "string"){
              cell.innerHTML = "<input id=\"" + key + "Input\" type=\"text\">"
            }else if(typeof data === "boolean"){
              cell.innerHTML = "<input id=\"" + key + "Input\" type=\"checkbox\">"
            }else if(typeof data === "number"){
              cell.innerHTML = "<input id=\"" + key + "Input\" type=\"number\">"
            }
          }

          console.log(tmpNametag)

          //if(tmpNametag){
          //  document.getElementById(key + "Input").value = tmpNametag[key];
          //}
    		}
      }

      frow.insertCell(-1).innerHTML = "<button onclick=\"updateNametag()\">Update</button>"
      frow.insertCell(-1).innerHTML = "<button onclick=\"clearTmpNametag()\">Clear</button>"

  	  for(i = 0; i < nametags.length; i++){

  		  var row = nametagTable.insertRow(i+1)

  		  for(var key in nametags[i]){
  		    if(nametags[i].hasOwnProperty(key)){
  			    var cell = row.insertCell(-1)
            var data = nametags[0][key]
            if(key.charAt(0) === "_"){
  			       cell.innerHTML = nametags[i][key]
            }else if(key === "ID"){
  			       cell.innerHTML = nametags[i][key]
            }else{
              if(typeof data === "string"){
    			         cell.innerHTML = nametags[i][key]
              }else if(typeof data === "boolean"){
    			         cell.innerHTML = nametags[i][key]
              }else if(typeof data === "number"){
    			         cell.innerHTML = nametags[i][key]
              }
            }
  		    }
  		  }

        row.insertCell(-1).innerHTML = "<button onclick=\"editNametag(" + nametags[i].ID + ")\">Edit</button>"
  	    row.insertCell(-1).innerHTML = "<button onclick=\"deleteNametag(" + nametags[i].ID + ")\">Delete</button>"

  	  }

  	  oldNametagJson = json
  	}
  }else{
    var l = nametagTable.rows.length

    for(i = 0; i < l; i++){
      nametagTable.deleteRow(0)
    }
    nametagTable.insertRow(0).innerHTML = "No Nametags"
  }
}

function updateNametag(){
  console.log("Submitting")

  for(var key in nametags[0]){
    if(nametags[0].hasOwnProperty(key)){
      var data = nametags[0][key]
      if(key.charAt(0) === "_"){
        tmpNametag[key] = document.getElementById(key + "Input").innerHTML;
      }else if(key === "ID"){
        tmpNametag[key] = Number(document.getElementById(key + "Input").innerHTML);
      }else{
        if(typeof data === "string"){
          tmpNametag[key] = document.getElementById(key + "Input").value;
        }else if(typeof data === "boolean"){
          tmpNametag[key] = document.getElementById(key + "Input").checked;
        }else if(typeof data === "number"){
          tmpNametag[key] = Number(document.getElementById(key + "Input").value);
        }
      }
    }
  }

  console.log(tmpNametag)
  var body = JSON.stringify(tmpNametag)
  console.log(body)
  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    updateNametags();
  }
  http.open("POST", "nametags/update", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
}

function clearTmpNametag(){
  for(var key in nametags[0]){
    if(nametags[0].hasOwnProperty(key)){
      var data = nametags[0][key]
      if(key.charAt(0) === "_"){
        document.getElementById(key + "Input").innerHTML = "";
      }else if(key === "ID"){
        document.getElementById(key + "Input").innerHTML = "";
      }else{
        if(typeof data === "string"){
          document.getElementById(key + "Input").value = "";
        }else if(typeof data === "boolean"){
          document.getElementById(key + "Input").checked = false;
        }else if(typeof data === "number"){
          document.getElementById(key + "Input").value = "";
        }
      }
    }
  }
}

function editNametag(id){
  console.log("Editing Nametag: " + id)
  var l = nametags.length
  for(var i = 0; i < l; i++){
    if(nametags[i].ID === id){
      //console.log("Nametag Found!")
      tmpNametag = nametags[i];
    }
  }

  for(var key in nametags[0]){
    if(nametags[0].hasOwnProperty(key)){
      var data = nametags[0][key]
      console.log(key)
      if(key.charAt(0) === "_"){
        document.getElementById(key + "Input").innerHTML = tmpNametag[key];
      }else if(key === "ID"){
        document.getElementById(key + "Input").innerHTML = tmpNametag[key];
      }else{
        if(typeof data === "string"){
          document.getElementById(key + "Input").value = tmpNametag[key];
        }else if(typeof data === "boolean"){
          document.getElementById(key + "Input").checked = tmpNametag[key];
        }else if(typeof data === "number"){
          document.getElementById(key + "Input").value = tmpNametag[key];
        }
      }
    }
  }
}

function deleteNametag(id){
  //console.log("Submitting")
  var nametag = {
    "id": ""
  }
  nametag.id = id
  //console.log(nametag.name)
  var body = JSON.stringify(nametag)
  //console.log(body)
  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    updateNametags();
  }
  http.open("POST", "nametags/delete", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
}


function updatePrinters(){
  //console.log("Updating Printers")
  phttp = new XMLHttpRequest()
  phttp.onreadystatechange = function (e){
    if(phttp.readyState === 4){
      processPrinters(phttp.responseText)
    }
  }
  phttp.open("GET", "printers", true)
  phttp.send();
}

function processPrinters(json){
  //console.log(json)
  if(json !== "[]" && json !== "null"){
  	if(json != oldPrinterJson){
	    printers = JSON.parse(json)

      var l = printerTable.rows.length

	    for(i = 0; i < l; i++){
		    printerTable.deleteRow(0)
	    }

	    var header = printerTable.createTHead()
	    var hrow = header.insertRow(0)

	    for(var key in printers[0]){
    		if(printers[0].hasOwnProperty(key)){
    		  var cell = hrow.insertCell(-1)
          if(key.charAt(0) === "_"){
            key = key.substring(1);
          }
    		  cell.innerHTML = "<b>" + key + "</b>"
          tmpNametag[key] = ""
    		}
  	  }

      hrow.insertCell(-1)
      hrow.insertCell(-1)

      var footer = printerTable.createTFoot()
	    var frow = footer.insertRow(-1)

      for(var key in printers[0]){
    		if(printers[0].hasOwnProperty(key)){
    		  var cell = frow.insertCell(-1)
          var data = printers[0][key]
          if(key.charAt(0) === "_"){
            cell.innerHTML = "<span id=\"" + key + "PrinterInput\"></span>"
          }else if(key === "ID"){
            cell.innerHTML = "<span id=\"" + key + "PrinterInput\"></span>"
          }else{
            if(typeof data === "string"){
              cell.innerHTML = "<input id=\"" + key + "PrinterInput\" type=\"text\">"
            }else if(typeof data === "boolean"){
              cell.innerHTML = "<input id=\"" + key + "PrinterInput\" type=\"checkbox\">"
            }else if(typeof data === "number"){
              cell.innerHTML = "<input id=\"" + key + "PrinterInput\" type=\"number\">"
            }
          }
    		}
      }

      frow.insertCell(-1).innerHTML = "<button onclick=\"updatePrinter()\">Update</button>"
      frow.insertCell(-1).innerHTML = "<button onclick=\"clearTmpPrinter()\">Clear</button>"

  	  for(i = 0; i < printers.length; i++){

  		  var row = printerTable.insertRow(i+1)

  		  for(var key in printers[i]){
  		    if(printers[i].hasOwnProperty(key)){
  			    var cell = row.insertCell(-1)
            var data = printers[0][key]
            if(key.charAt(0) === "_"){
  			       cell.innerHTML = printers[i][key]
            }else if(key === "ID"){
  			       cell.innerHTML = printers[i][key]
            }else{
              if(typeof data === "string"){
    			         cell.innerHTML = printers[i][key]
              }else if(typeof data === "boolean"){
    			         cell.innerHTML = printers[i][key]
              }else if(typeof data === "number"){
    			         cell.innerHTML = printers[i][key]
              }
            }
  		    }
  		  }

        row.insertCell(-1).innerHTML = "<button onclick=\"editPrinter(" + printers[i].ID + ")\">Edit</button>"
  	    row.insertCell(-1).innerHTML = "<button onclick=\"deletePrinter(" + printers[i].ID + ")\">Delete</button>"

  	  }

  	  oldPrinterJson = json
  	}
  }else{
    //console.log("No Printers")
    var l = printerTable.rows.length

    for(i = 0; i < l; i++){
      printerTable.deleteRow(0)
    }
    printerTable.insertRow(0).innerHTML = "No Printers"
    //printerTable.insertRow(1).innerHTML = "<button onclick=\"updatePrinter(0)\">Add Printer</button>"
  }
}

function updatePrinter(){
  console.log("Submitting")

  for(var key in printers[0]){
    if(printers[0].hasOwnProperty(key)){
      var data = printers[0][key]
      if(key.charAt(0) === "_"){
        tmpPrinter[key] = document.getElementById(key + "PrinterInput").innerHTML;
      }else if(key === "ID"){
        tmpPrinter[key] = Number(document.getElementById(key + "PrinterInput").innerHTML);
      }else{
        if(typeof data === "string"){
          tmpPrinter[key] = document.getElementById(key + "PrinterInput").value;
        }else if(typeof data === "boolean"){
          tmpPrinter[key] = document.getElementById(key + "PrinterInput").checked;
        }else if(typeof data === "number"){
          tmpPrinter[key] = Number(document.getElementById(key + "PrinterInput").value);
        }
      }
    }
  }

  console.log(tmpPrinter)
  var body = JSON.stringify(tmpPrinter)
  console.log(body)
  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    updatePrinters();
  }
  http.open("POST", "printers/update", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
}

function clearTmpPrinter(){
  for(var key in printers[0]){
    if(printers[0].hasOwnProperty(key)){
      var data = printers[0][key]
      if(key.charAt(0) === "_"){
        document.getElementById(key + "PrinterInput").innerHTML = "";
      }else if(key === "ID"){
        document.getElementById(key + "PrinterInput").innerHTML = "";
      }else{
        if(typeof data === "string"){
          document.getElementById(key + "PrinterInput").value = "";
        }else if(typeof data === "boolean"){
          document.getElementById(key + "PrinterInput").checked = false;
        }else if(typeof data === "number"){
          document.getElementById(key + "PrinterInput").value = "";
        }
      }
    }
  }
}

function editPrinter(id){
  //console.log("Editing Nametag: " + id)
  var l = printers.length
  for(var i = 0; i < l; i++){
    if(printers[i].ID === id){
      tmpPrinter = printers[i];
    }
  }

  for(var key in printers[0]){
    if(printers[0].hasOwnProperty(key)){
      var data = printers[0][key]
      console.log(key)
      if(key.charAt(0) === "_"){
        document.getElementById(key + "PrinterInput").innerHTML = tmpPrinter[key];
      }else if(key === "ID"){
        document.getElementById(key + "PrinterInput").innerHTML = tmpPrinter[key];
      }else{
        if(typeof data === "string"){
          document.getElementById(key + "PrinterInput").value = tmpPrinter[key];
        }else if(typeof data === "boolean"){
          document.getElementById(key + "PrinterInput").checked = tmpPrinter[key];
        }else if(typeof data === "number"){
          document.getElementById(key + "PrinterInput").value = tmpPrinter[key];
        }
      }
    }
  }
}

function deletePrinter(id){
  //console.log("Submitting")
  var printer = {
    "id": ""
  }
  printer.id = id
  //console.log(nametag.name)
  var body = JSON.stringify(printer)
  //console.log(body)
  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    updatePrinters();
  }
  http.open("POST", "printers/delete", true)
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

function showBoth(){
    nametagDiv.style.display = "block"
    printerDiv.style.display = "block"
}
