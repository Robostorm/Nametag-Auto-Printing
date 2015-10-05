
var nametagDiv = document.getElementById("nametags")
var printerDiv = document.getElementById("printers")
var nametagTable = document.getElementById("nametagTable")
var printerTable = document.getElementById("printerTable")

var nametags
var tmpNametag
var oldNametagJson = "null"
var printers
var oldPrinterJson = "null"

updateNametags();
//updatePrinters();

window.setInterval("update()", 1000)

showNametags()

function update(){
  //console.log("Updating")
  updateNametags();

  phttp = new XMLHttpRequest()
  phttp.onreadystatechange = function (e){
	 if(phttp.readyState == 4){
	    //processPrinters(phttp.responseText)
	   }
  }
  phttp.open("GET", "printers", true)
  phttp.send();

}

function updateNametags(){
  nhttp = new XMLHttpRequest()
  nhttp.onreadystatechange = function (e){
    if(nhttp.readyState == 4){
      processNametags(nhttp.responseText)
    }
  }
  nhttp.open("GET", "nametags", true)
  nhttp.send();
}

function processNametags(json){

  if(json !== "[]"){
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
    		  cell.innerHTML = "<b>" + key + "</b>"
    		}
  	  }

      var footer = nametagTable.createTFoot()
	    var frow = footer.insertRow(-1)

      for(var key in nametags[0]){
    		if(nametags[0].hasOwnProperty(key)){
    		  var cell = frow.insertCell(-1)
          var data = nametags[0][key]
          if(key === "ID"){
            console.log(key)
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
  			    cell.innerHTML = nametags[i][key]
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
      if(key === "ID"){
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
    console.log("DONE")
    console.log(http.responseText)
  }
  http.open("POST", "nametags/update", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
}

function clearTmpNametag(){
  for(var key in nametags[0]){
    if(nametags[0].hasOwnProperty(key)){
      var data = nametags[0][key]
      if(key === "ID"){
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
  //console.log("Editing Nametag: " + id)
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
      if(key === "ID"){
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


function showNametags(){
    nametagDiv.style.display = "block"
    printerDiv.style.display = "none"
}

function showPrinters(){
    nametagDiv.style.display = "none"
    printerDiv.style.display = "block"
}
