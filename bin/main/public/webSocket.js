//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://localhost:4567/socket");
		//"ws://" + location.hostname + ":" + location.port + "/chat/"
webSocket.onmessage = function (msg) { updateChat(msg); };
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
	targetBoard(id("columnInput").value, id("lineInput").value); 
});


function targetBoard(column, line){
	if (column !== "" && line != ""){
		msg = " a tire sur : "+ column + ", "+ line + "     ? touche, coule, non touche ?";
		data = {};
		data["type"] = 2;
		data["message"] = msg;
		data = JSON.stringify(data)
		webSocket.send(data);
		id("columnInput").value = ""; 
		id("lineInput").value = "";
	}
}

//id("validateName").addEventListener("click", function(){
//	console.log("ok validateName")
//	newPlayer(id("playerName"));
//})
//
//function newPlayer(name){
//	if (name !== ""){
//		msg = name;
//		data = {};
//		data["type"] = 3;
//		data["message"] = msg;
//		data = JSON.stringify(data)
//		webSocket.send(data);
//	}
//}



