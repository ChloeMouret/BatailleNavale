
//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

id("validateName").addEventListener("click", function(){
	console.log("ok validateName")
	newPlayer(id("playerName"));
})

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