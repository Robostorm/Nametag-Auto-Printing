
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

window.onload = function(){
  getPrinters()
}

function getPrinters(){

  http = new XMLHttpRequest()

  http.onreadystatechange = function (e){
    if(http.readyState == 4){

      if(http.responseText != oldJsonPrinters){
        printers = JSON.parse(http.responseText)

        colors.innerHTML = "<input type=radio name=color value=0 checked=true id=defaultColor class=color>Any</input>"

        for(var i = 0; i < printers.length; i++){
          if(printers[i].selectable === true){
            colors.innerHTML += "<input type=radio name=color value=" + printers[i].id + " class=color>" + printers[i].color + "</input>"
          }
        }
        oldJsonPrinters = http.responseText
      }
    }
  }

  http.open("GET", "printers", true)
  http.send()
}

function imgChecker(){

  getPrinters()

  if(imgUrl != oldImgUrl || !imgFound){
    imgFound = false
    if(fileExists(imgUrl)){
      console.log("Image Exists!")
      img.src = imgUrl
      currentStatus.innerHTML = "Ready."
      imgFound = true
    }else{
      console.log("Image Does Not Exist!")
    }
    oldImgUrl = imgUrl
  }else{
    if(currentStatus.innerHTML == "Loading..."){
      currentStatus.innerHTML = "Ready."
    }
  }
  input.focus()
}

function submit(){
  console.log("Submitting")
  if(!nameValid(input.value)){
    console.log("Name not valid!")
    return;
  }
  input.focus()
  var nametag = {
    "name": "",
    "printer-id": ""
  }
  nametag.name = input.value

  for (var i = 0, length = colorRadios.length; i < length; i++) {
    if (colorRadios[i].checked) {
        console.log(colorRadios[i].value)
        nametag["printer-id"] = colorRadios[i].value
        break
    }
}

  //console.log(nametag.name)
  var body = JSON.stringify(nametag)
  console.log(body)
  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    console.log("DONE")
    console.log(http.responseText)
    input.value = ""
    currentStatus.innerHTML = "Submitted Sucessfully"
    imgUrl = blankUrl
    input.focus()
    getPrinters()
    document.getElementById("defaultColor").checked = true
  }
  http.open("POST", "submit", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
}

function preview(){
  console.log("Previewing")
  input.focus()

  if(!nameValid(input.value)){
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
  http = new XMLHttpRequest()
  http.onreadystatechange = function (e){
    if(http.readyState == 4){
      console.log("DONE")
      console.log(http.readyState)
      console.log(http.responseText)
      console.log(http.status)
      imgUrl = http.responseText
      input.focus()
    }
  }
  http.open("POST", "preview", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
  currentStatus.innerHTML = "Loading..."
}

function nameValid(name){

  if(name.length > 10){
    currentStatus.innerHTML = "Name is too long! Please enter a name that is less than or equal to 10 characters."
    return false
  }

  if(name == ""){
    currentStatus.innerHTML = "Name is empty. Please enter a name."
    return false
  }

  if(name.indexOf("\\") > -1 || name.indexOf("#") > -1 || name.indexOf("%") > -1){
    currentStatus.innerHTML = "Name has \\, #, or %. Please remove all \\, #, and %"
    return false
  }

  return true
}

function fileExists(image_url){

  var http = new XMLHttpRequest();

  http.open('HEAD', image_url, false);
  http.send();

  console.log(http.status)

  return http.status < 400;

}
