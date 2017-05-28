var input = document.getElementById("input")
var img = document.getElementById("img")
var currentStatus = document.getElementById("status")
var colors = document.getElementById("colors")
var colorRadios = document.getElementsByName("color");
var defaultColor = document.getElementById("defaultColor")

var blankUrl = "assets/images/blank.png"
var imgUrl = blankUrl
var oldImgUrl = imgUrl
var imgFound = false

var printers = {}
var oldJsonPrinters = ""

window.setInterval("imgChecker()", 1000);

window.onload = function() {
  getPrinters()
}

function getPrinters() {

  printerhttp = new XMLHttpRequest()

  printerhttp.onreadystatechange = function(e) {
    if (printerhttp.readyState == 4) {

      if (printerhttp.responseText != oldJsonPrinters) {
        printers = JSON.parse(printerhttp.responseText)

        colors.innerHTML = ""

        createColor(0, "Any", true)

        for (var i = 0; i < printers.length; i++) {
          if (printers[i].selectable === true) {
            createColor(printers[i].id, printers[i].color, false)
          }
        }
        oldJsonPrinters = printerhttp.responseText
      }
    }
  }

  printerhttp.open("GET", "printers", true)
  printerhttp.send()
}

function createColor(id, color, d) {
  var radio = document.createElement("input");
  radio.type = "radio";
  radio.name = "color";
  radio.value = id;
  radio.color = color;

  if(d){
    radio.id = "defaultColor"
    radio.checked = true
  }

  var label = document.createElement("label");
  label.className = "colorbtn"
  label.onClick = input.focus
  label.appendChild(radio);
  label.appendChild(document.createTextNode(color));

  colors.appendChild(label)
}

function imgChecker() {

  getPrinters()

  if (imgUrl != oldImgUrl || !imgFound) {
    imgFound = false
    if (fileExists(imgUrl)) {
      console.log("Image Exists!")
      img.src = imgUrl
      currentStatus.innerHTML = "Ready."
      imgFound = true
    } else {
      console.log("Image Does Not Exist!")
    }
    oldImgUrl = imgUrl
  } else {
    if (currentStatus.innerHTML == "Loading...") {
      currentStatus.innerHTML = "Ready."
    }
  }
  input.focus()
}

function submit() {
  console.log("Submitting")
  if (!nameValid(input.value)) {
    console.log("Name not valid!")
    return;
  }
  input.focus()
  var nametag = {
    "name": "",
    "printer-id": "",
    "color": ""
  }
  nametag.name = input.value

  for (var i = 0, length = colorRadios.length; i < length; i++) {
    if (colorRadios[i].checked) {
      console.log(colorRadios[i].value)
      nametag["printer-id"] = colorRadios[i].value
      nametag["color"] = colorRadios[i].color;
      console.log(nametag);
      break
    }
  }

  //console.log(nametag.name)
  var body = JSON.stringify(nametag)
  console.log(body)
  sumbithttp = new XMLHttpRequest()
  sumbithttp.onreadystatechange = function(e) {
    console.log("DONE")
    console.log(sumbithttp.responseText)
    console.log(sumbithttp.status)
    input.value = ""
    currentStatus.innerHTML = "Submitted Sucessfully"
    imgUrl = blankUrl
    input.focus()
    getPrinters()
    document.getElementById("defaultColor").checked = true
  }
  sumbithttp.open("POST", "submit", true)
  sumbithttp.setRequestHeader("Content-type", "application/json")
  sumbithttp.send(body)
}

function preview() {
  console.log("Previewing")
  input.focus()

  if (!nameValid(input.value)) {
    console.log("Name not valid!")
    return;
  }

  var nametag = {
    "name": ""
  }
  nametag.name = input.value
    //console.log(nametag.name)

  var body = JSON.stringify(nametag)
  console.log(body)
  previewhttp = new XMLHttpRequest()
  previewhttp.onreadystatechange = function(e) {
    if (previewhttp.readyState == 4) {
      console.log("DONE")
      console.log(previewhttp.readyState)
      console.log(previewhttp.responseText)
      console.log(previewhttp.status)
      imgUrl = previewhttp.responseText
      input.focus()
    }
  }
  previewhttp.open("POST", "preview", true)
  previewhttp.setRequestHeader("Content-type", "application/json")
  previewhttp.send(body)
  currentStatus.innerHTML = "Loading..."
}

function nameValid(name) {

  if (name.length > 10) {
    currentStatus.innerHTML = "Name is too long! Please enter a name that is less than or equal to 10 characters."
    return false
  }

  if (name == "") {
    currentStatus.innerHTML = "Name is empty. Please enter a name."
    return false
  }

  if (name.indexOf("\\") > -1 || name.indexOf("#") > -1 || name.indexOf("%") > -1) {
    currentStatus.innerHTML = "Name has \\, #, or %. Please remove all \\, #, and %"
    return false
  }

  return true
}

function fileExists(image_url) {

  var http = new XMLHttpRequest();

  http.open('HEAD', image_url, false);
  http.send();

  console.log(http.status)

  return http.status < 400;

}
