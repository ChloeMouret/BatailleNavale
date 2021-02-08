//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://localhost:4567/socket");
		//"ws://" + location.hostname + ":" + location.port + "/chat/"
webSocket.onmessage = function (msg) { 
	var json = JSON.parse(msg.data);
	if (json.type =="game"){
		updateTargetBoard(msg);
	}
	else if (json.type="message"){
		console.log("message");
		updateChat(msg); 
	}
};

webSocket.onclose = function () { alert("WebSocket connection closed test") };

webSocket.onopen = function () { console.log("Websocket connected !"); };

//Send message if "Send" is clicked
id("send").addEventListener("click", function () {
    sendMessage(id("message").value);
});

//Send message if enter is pressed in the input field
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { sendMessage(e.target.value); }
});

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
	console.log("ok send message")
    if (message !== "") {
    	json = {};
		json["type"] = 1;
		json["message"] = message;
		jjson = JSON.stringify(json)
        webSocket.send(jjson);
        id("message").value = "";
    }
}

//Change color of case when already fired 
function updateTargetBoard(msg){
	var json = JSON.parse(msg.data);
	if (json.status == 0){
		id("cell-"+json.line+"-"+json.column+"").style.backgroundColor = 'blue';
	}
	else {
		id("cell-"+json.line+"-"+json.column+"").style.backgroundColor = 'red';
	}
}


//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var json = JSON.parse(msg.data);
    $('#chat').append(json.userMessage).scrollTop($("#chat")[0].scrollHeight);
    //insert("chat", data.userMessage);
    id("userlist").innerHTML = "";
    console.log(json.userlist["0"]["name"]);
    json.userlist.forEach(function (user) {
        insert("userlist", "<li>" + user["name"] + "</li>");
        console.log("Apres Userlist");
    });
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

//send message userX has fired
id("validateButton").addEventListener("click", function() {
	//id("cell-0-0").style.backgroundColor = "green";
	targetBoard(id("columnInput").value, id("lineInput").value); 
});


function targetBoard(column, line){
	if (column !== "" && line != ""){
		data = {};
		data["type"] = 3;
		data["column"] = column;
		data["line"]=line;
		data = JSON.stringify(data)
		webSocket.send(data);
		id("columnInput").value = "";
		id("lineInput").value = "";
	}
}


function newPlayer(name){
	if (name !== ""){
		msg = name;
		data = {};
		data["type"] = 3;
		data["message"] = msg;
		data = JSON.stringify(data)
		webSocket.send(data);
	}
}

id("playerReady").addEventListener("click", function(){
	createBoard(5,5, "grid");
})


function createBoard(nbColumn, nbLine, div){
	//2 first lines in order to not multiply the grid
	//id("node").removeChild(id(div))
	//insert("node", "<div id='"+div+"' class='grid' style='width: auto'></div>")
	for (var i=0; i<nbLine; i++){
		insert(div, "<div style='display:flex; flex-direction : horizontal; width :"+(30+50*nbColumn)+"px'>" +
				"<div class=idCellVerti' style='width:30px; background-color : #ebf5fb; text-align : center; border:1px solid black" +
				"'>"+(nbLine-i-1)+"</div><div id = 'row" +(nbLine-i-1)+"' class='row' style='width :"+(50*nbColumn)+"px'></div></div>")
		for (var j=0; j<nbColumn; j++){
			insert("row"+(nbLine-i-1)+"", "<div id = 'cell-"+(nbLine-i-1)+"-"+(nbColumn-j-1)+"' class='cell'></div>")
		}
	}
	//insert first line which is numeros of columns
	insert(div, "<div id = 'rowIni' style='height : 30px; width :"+(50*nbColumn)+"px; margin-left : 30px'></div>")
	for (var j=1; j<nbColumn+1; j++){
		insert("rowIni", "<div class='idCellHori'> "+ (nbColumn-j) +"</div>")
	}
}


//In order to have only vertical or horizontal checked when choosing boats positions
$('input[type="checkbox"]').on('change', function() {
	   $('input[type="checkbox"]').not(this).prop('checked', false);
	});

id("next").addEventListener("click", function(){
	var column = id("columnChoiceInput").value; 
	var line = id("lineChoiceInput").value;
	choose1BoatPosition(column, line); 
})

//0 = Vertical, 1=Horizontal
function choose1BoatPosition(column, line){
	var direction; 
	if (id("verticalCheckbox").checked){
		direction = 0; 
	}
	else {
		direction = 1; 
	}
	if ((column !== "" ) && (line !== "")){
		data = {};
		data["type"] = 2;
		data["column"] = column;
		data["line"]=line; 
		data["direction"]=direction; 
		data = JSON.stringify(data)
		webSocket.send(data);
		id("columnInput").value = ""; 
		id("lineInput").value = "";
	}
	id("columnChoiceInput").value = "";
	id("lineChoiceInput").value = "";
	//id("verticalCheckbox").prop('checked', false);
	//id("horizontalCheckbox").prop('checked', false);
}




