//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://localhost:4567/batailleNavale");
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
    if (message !== "") {
    	data = {};
		data["type"] = 1;
		data["message"] = message;
		data = JSON.stringify(data)
        webSocket.send(data);
        id("message").value = "";
    }
}

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
    var data = JSON.parse(msg.data);
    $('#chat').append(data.userMessage).scrollTop($("#chat")[0].scrollHeight);
    //insert("chat", data.userMessage);
    id("userlist").innerHTML = "";
    data.userlist.forEach(function (user) {
        insert("userlist", "<li>" + user + "</li>");
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
		data["type"] = 0;
		data["message"] = msg;
		data = JSON.stringify(data)
		webSocket.send(data);
		id("columnInput").value = ""; 
		id("lineInput").value = "";
	}
}




