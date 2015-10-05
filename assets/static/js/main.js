
var input = document.getElementById("input")
var img = document.getElementById("img")
var currentStatus = document.getElementById("status");

var blankUrl = "assets/images/blank.png"
var imgUrl = blankUrl
var oldImgUrl = imgUrl
var imgFound = false

window.setInterval("imgChecker()", 1000);

function imgChecker(){
  if(imgUrl != oldImgUrl || !imgFound){
    imgFound = false
    if(fileExists(imgUrl)){
      console.log("Image Exists!")
      img.src = imgUrl
      currentStatus.innerHTML = ""
      imgFound = true
    }else{
        console.log("Image Does Not Exist!")
    }
    oldImgUrl = imgUrl
  }else{
    if(currentStatus.innerHTML == "Loading..."){
      currentStatus.innerHTML = ""
    }
  }
  input.focus()
}

function submit(){
  console.log("Submitting")
  var nametag = {
    "name": ""
  }
  nametag.name = input.value
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
  }
  http.open("POST", "submit", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
}

function preview(){
  console.log("Previewing")

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
    }
  }
  http.open("POST", "preview", true)
  http.setRequestHeader("Content-type","application/json")
  http.send(body)
  currentStatus.innerHTML = "Loading..."
}

function nameValid(name){

  if(name.length > 10){
    currentStatus.innerHTML = "Name is too long! Please enter a name that is less than or equal to 10 characters"
    return false
  }

  if(name == ""){
    currentStatus.innerHTML = "Name is empty. Please enter a name."
    return false
  }

  return name.length <= 10 && name != ""
}

function fileExists(image_url){

    var http = new XMLHttpRequest();

    http.open('HEAD', image_url, false);
    http.send();

    console.log(http.status)

    return http.status < 400;

}