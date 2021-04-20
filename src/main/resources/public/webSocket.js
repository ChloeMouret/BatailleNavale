//Establish the WebSocket connection and set up event handlers
var host = location.origin.replace(/^http/, 'ws')+"/socket";
var webSocket = new WebSocket(host);
console.log("Connecting websocket to "+host);
var error = 0; 
		//"ws://" + location.hostname + ":" + location.port + "/chat/"
webSocket.onmessage = function (msg) { 
	var json = JSON.parse(msg.data);
	if (json.type =="game"){
		updateTargetBoard(msg);
	}
	else if (json.type=="message"){
		updateChat(msg); 
	}
	else if (json.type=="turn"){
		console.log("firstPlayer");
		myTurn=true; 
		id("validateButton").style.display = "block";
		id("validateButton").innerHTML = "Cliquez où vous voulez tirer"; 
		id("validateButton").style.pointerEvents = "none";
	}
	else if (json.type == "not-turn"){
		console.log("not my turn");
		myTurn=false; 
		id("validateButton").style.display = "block";
		id("validateButton").innerHTML = "Ce n'est pas ton tour"; 
		id("validateButton").style.pointerEvents = "none";
		id("validateButton").style.backgroundColor = "white";
	}
	else if (json.type=="init"){
		console.log("init");
		$("#modalPlaceBoats").modal('show');
		createOnce(json); 
	}
	else if (json.type=="boats-ok"){
		waitingOtherPlayer(msg);
	}
	else if (json.type=="winner"){
		$("#modalWinner").modal('show');
		confetti.start();  
	}
	else if (json.type=="loser"){
		$("#modalLoser").modal('show');
	}
	else if (json.type=="error"){
		error = 1;
		if (json.error=="column-line-not-integer"){
			console.log("in error : column & line not integer")
			$.notify("Vous devez choisir un chiffre entre 0 et "+json.verticalBoardSize+" pour la ligne", {position : "top center"});
			$.notify("Vous devez choisir un chiffre entre 0 et "+json.horizontalBoardSize+" pour la colonne", {position : "top center"});
		}
		else if (json.error=="column-not-interger"){
			$.notify("Vous devez choisir un chiffre entre 0 et "+json.horizontalBoardSize+" pour la colonne", {position : "top center"});
		}
		else if (json.error=="line-not-integer"){
			$.notify("Vous devez choisir un chiffre entre 0 et "+json.verticalBoardSize+" pour la ligne", {position : "top center"});
		}
	}
};

webSocket.onclose = function () { alert("WebSocket connection closed test") };

webSocket.onopen = function () { console.log("websocket connected")};

var boats = {};
var boatsPlaced = 0;
var boardVerticalSize = 0;
var boardHorizontalSize = 0; 
var columnChoice = -1;
var lineChoice = -1; 
var listIdBoatsPlaced = []; 
var columnTarget = -1; 
var lineTarget = -1; 
var previousTarget = ""; 
var previousColor = "grey"; 
var myTurn = false; 


var createOnce = function(json){
	var executed = false;
	if (!executed){
		executed = true;
		createBoard(json.width, json.height, "grid", "init");
		createBoard(json.width, json.height, "targetBoard", "cell");
		createBoard(json.width, json.height, "yourBoard", "yourCell");
		id("header1").innerHTML = "<i> Vous avez "+ json.boats.length +" bateaux a placer </i>";
		id("p1").innerHTML = "Votre bateau n°1 a une longueur de <b style='color:red'>"+json.boats[0]+"</b> cases";
		boats = json.boats;
		boardVerticalSize = json.height; 
		boardHorizontalSize = json.width; 
		createMoovingDiv(1); 
	}
}

var elements = document.getElementsByClassName("celltargetBoard"); 

function getHTMLCollectionelements(){
	for (var i = 0; i < elements.length; i++) {
	    elements[i].addEventListener("click", function () {
	    	columnTarget = getColumnFromId($(this).get(0).id); 
	    	lineTarget = getLineFromId($(this).get(0).id); 
	    	console.log(columnTarget);
	    	console.log(lineTarget);
//	    	id("columnInput").value = columnTarget; 
//	    	id("lineInput").value = lineTarget;
	    	if (myTurn){
	    		id("validateButton").innerHTML = "Tirer en : \n Colonne : "+columnTarget+"\n Ligne : "+lineTarget + ""; 
		    	id("validateButton").style.backgroundColor = 'red';
		    	id("validateButton").style.pointerEvents = "auto";
		    	if (previousTarget!=""){
		    		id(previousTarget).style.backgroundColor = previousColor; 
		    	}
		    	previousTarget="cell-"+lineTarget+"-"+columnTarget+""; 
		    	previousColor=id(previousTarget).style.backgroundColor; 
		    	id(previousTarget).style.backgroundColor = 'purple';
	    	}
	    	else {
	    		columnTarget=-1; 
	    		lineTarget=-1; 
	    	}
	    });
	}
}

function getColumnFromId(id){
	return id.charAt(id.length-1); 
}

function getLineFromId(id){
	return id.charAt(id.length-3);
}

//O=Vertical, 1=Horizontal
function createMoovingDiv(direction){
	var boatSize = boats[boatsPlaced];
	id("container").innerHTML = "<div id='moovingDiv"+boatsPlaced+"' style='display:flex; flex-direction : horizontal; width :100px'>";
	if (direction == 0){
		id("moovingDiv"+boatsPlaced).style.height = 50*boatSize+"px";
		id("moovingDiv"+boatsPlaced).style.width = "50px"; 
		id("moovingDiv"+boatsPlaced).style.display = "block"; 
		if (boatSize == 1){
			id("moovingDiv"+boatsPlaced).innerHTML = "<div id='moovingCell' class='boat'></div>";
		}
		else {
			id("moovingDiv"+boatsPlaced).innerHTML = "<div id='moovingCell' class='boat'></div>";
			for (var i=0; i<boatSize-1; i++){
				id("moovingDiv"+boatsPlaced).innerHTML = id("moovingDiv"+boatsPlaced).innerHTML + "<div style='margin-top: -6px' class='boat'></div>"; 
			}
		}
		id("grid").innerHTML = ""; 
		createBoard(boardHorizontalSize ,boardVerticalSize , "grid", "init");
		verticalDrag(boatSize);
	}
	else {
		id("moovingDiv"+boatsPlaced).style.width = 50*boatSize+"px";
		if (boatSize == 1){
			id("moovingDiv"+boatsPlaced).innerHTML = "<div id='moovingCell' class='boat'></div>";
		}
		else {
			id("moovingDiv"+boatsPlaced).innerHTML = "<div id='moovingCell' class='boat' style='width : 50px'></div>";
			for (var i=0; i<boatSize-1; i++){
				id("moovingDiv"+boatsPlaced).innerHTML = id("moovingDiv"+boatsPlaced).innerHTML + "<div class='boat'></div>"; 
			}
		}
		id("grid").innerHTML = ""; 
		createBoard(boardHorizontalSize ,boardVerticalSize , "grid", "init");
		horizontalDrag(boatSize);
	}
}

//O=Vertical, 1=Horizontal
function changeMoovingDiv(direction){
	if (direction == 0){
		id("container").innerHTML = "<div id='moovingDiv"+boatsPlaced+"' style='width :50px; height: auto'>"; 
		createMoovingDiv(direction);
	}
	else {
		id("container").innerHTML = "<div id='moovingDiv"+boatsPlaced+"' style='display:flex; flex-direction : horizontal; width :auto'></div>"; 
		createMoovingDiv(direction);
	}
}

//O=Vertical, 1=Horizontal
function unableDroppable(boatSize, direction) {
	if (direction == 0){
		for (var j=0; j<boardVerticalSize; j++){
			for (var k=0; k<boardHorizontalSize; k++){
				if (k>(boardHorizontalSize-boatSize)) {
					id("init-"+k+"-"+j).className = "cellNoDroppable"
				}
				else {
					id("init-"+k+"-"+j).className = "cellD"
				}
			}
		}
		if (listIdBoatsPlaced.length !=0){
			unableDroppableOnBoat(boatSize, direction); 
		}
	}
	else if (direction ==1){
		for (var j=0; j<boardVerticalSize; j++){
			for (var k=0; k<boardHorizontalSize; k++){
				if (j>(boardHorizontalSize-boatSize)){
					id("init-"+k+"-"+j).className = "cellNoDroppable"
				}
				else {
					id("init-"+k+"-"+j).className = "cellD"
				}
			}
		}
		if (listIdBoatsPlaced.length !=0){ 
			console.log("ok")
			unableDroppableOnBoat(boatSize, direction); 
		}
	}
	else {
		console.log("Not good direction"); 
	}
}

function unableDroppableOnBoat(boatSize, direction){
	for (var i=0; i<listIdBoatsPlaced.length; i++){ 
		var column=parseInt(getColumnFromId(listIdBoatsPlaced[i])); 
		var line=parseInt(getLineFromId(listIdBoatsPlaced[i])); 
		if (direction == 0){
			if ((line-boatSize+1)>=0) {
				for (var j=0; j<boatSize; j++) {
					id("init-"+(line-j)+"-"+column).className = "cellNoDroppable"
				}
			}
			else {
				if (line != 0){
					for (var k=0; k<(line+1); k++) {
						id("init-"+(line-k)+"-"+column).className = "cellNoDroppable"; 
					}
				}
				else {   //line = 0 
					id("init-"+line+"-"+column).className = "cellNoDroppable"; 
				}
			}
		}
		else {
			if ((column-boatSize+1)>=0) {
				for (var j=0; j<boatSize; j++) {
					id("init-"+line+"-"+(column-j)).className = "cellNoDroppable"
				}
			}
			else {
				if (column != 0){
					for (var k=0; k<(column+1); k++) {
						id("init-"+line+"-"+(column-k)).className = "cellNoDroppable"; 
					}
				}
				else {   //column = 0 
					id("init-"+line+"-"+column).className = "cellNoDroppable"; 
				}
			}
		}
	}
}

function horizontalDrag(boatSize) {
	unableDroppable(boatSize, 1); 
	$("#moovingDiv"+boatsPlaced).draggable({
		revert: "invalid", 
		cursor: "move", 
		cursorAt: { top: 25, left: 25 }
	});
	$(".cellD").droppable({
		tolerance: "pointer",
		drop: function(event, ui) {
			var $this = $(this);
//			var positionDiv = document.getElementById($(this).get(0).id).getBoundingClientRect();
//			var top = positionDiv.top;
//			var left = positionDiv.left; 
			columnChoice=getColumnFromId($(this).get(0).id); 
			lineChoice=getLineFromId($(this).get(0).id);
			id("next").disabled = false; 
			//$(this).css("background-color","green");
			tolerance: "pointer",
		    ui.draggable.position({
		    	my: "left-"+(100/(boatSize*2))+"%",
		    	at: "center",
		    	of: $this,
		    	using: function(pos) {
		    		$(this).animate(pos, 200, "linear");
		    	}
		    });
		 }
	});
}

function verticalDrag(boatSize) {
	unableDroppable(boatSize, 0);
	$("#moovingDiv"+boatsPlaced).draggable({
		revert: "invalid", 
		cursor: "move", 
		cursorAt: { top: 25, left: 25 }
	});
	$(".cellD").droppable({
		tolerance: "pointer",
		drop: function(event, ui) {
			var $this = $(this);
			columnChoice=getColumnFromId($(this).get(0).id); 
			lineChoice=getLineFromId($(this).get(0).id); 
			id("next").disabled = false; 
			//$(this).css("background-color","green");
			tolerance: "pointer",
		    ui.draggable.position({
		    	my: "top-"+(100/(boatSize*2))+"%",
		    	at: "center",
		    	of: $this,
		    	using: function(pos) {
		    		$(this).animate(pos, 0, "linear");
		    	}
		    });
		 }
	});
}



function waitingOtherPlayer(msg) {
	var json = JSON.parse(msg.data);
	if (json.otherPlayerReady == "no"){
		$("#modalPlaceBoats").modal('hide');
		console.log("other player not ready");
		$("#modalWaiting").modal('show');
	}
	else {
		console.log("both ready");
		$("#modalPlaceBoats").modal('hide');
		$("#modalWaiting").modal('hide');
	}
}

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
    json.userlist.forEach(function (user) {
    	console.log(user["name"]);
        insert("userlist", "<li>" + user["name"] + "</li>");
    });
    id("gameIdentity").innerHTML = "Game Id : <b>"+ json.gameId +"</b>";
    
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
	if (columnTarget!=-1 && lineTarget!=-1){
		targetBoard(columnTarget, lineTarget); 
		//id("validateButton").innerHTML = "Cliquez où vous voulez tirer"; 
		//id("validateButton").style.backgroundColor = 'white';
		columnTarget=-1;
		lineTarget=-1;
		previousTarget=""; 
		nextPlayer();
	}
});

//TODO
id("copyButton").addEventListener("click", function () {
	let temp = id("<input>");
	console.log("in1");
	$("body").append(temp);
	console.log("in2");
    temp.val(gameIdentity).select();
    console.log("in3");
    document.execCommand("copy");
    temp.remove();
})


function nextPlayer(){
	data = {};
	data["type"] = 4;
	data = JSON.stringify(data)
	webSocket.send(data);
}


function targetBoard(column, line){
	console.log("in target Board"); 
	if (column !== "" && line != ""){
		data = {};
		data["type"] = 3;
		data["column"] = column;
		data["line"]=line;
		data = JSON.stringify(data)
		webSocket.send(data);
//		id("columnInput").value = "";
//		id("lineInput").value = "";
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

//id("playerReady").addEventListener("click", function(){
//	createBoard(5,5, "grid");
//})


function createBoard(nbColumn, nbLine, div, id){
	//2 first lines in order to not multiply the grid
	//id("node").removeChild(id(div))
	//insert("node", "<div id='"+div+"' class='grid' style='width: auto'></div>")
	console.log("in create Board");
	for (var i=0; i<nbLine; i++){
		insert(div, "<div style='display:flex; flex-direction : horizontal; width :"+(30+50*nbColumn)+"px'>" +
				"<div class=idCellVerti style='width:30px; background-color : #ebf5fb; text-align : center; border:1px solid black" +
				"'>"+(nbLine-i-1)+"</div><div id = 'row-"+div+"-" +(nbLine-i-1)+"' class='row' style='width :"+(50*nbColumn)+"px'></div></div>")
		for (var j=0; j<nbColumn; j++){
			insert("row-"+div+"-"+(nbLine-i-1)+"", "<div id = '"+id+"-"+(nbLine-i-1)+"-"+(nbColumn-j-1)+"' class='cell"+div+"'></div>")
		}
	}
	//insert first line which is numeros of columns
	insert(div, "<div id = 'rowIni-"+div+"' style='height : 30px; width :"+(50*nbColumn)+"px; margin-left : 30px'></div>")
	for (var j=1; j<nbColumn+1; j++){
		insert("rowIni-"+div+"", "<div class='idCellHori'> "+ (nbColumn-j) +"</div>")
	}
	if (listIdBoatsPlaced.length != 0){
		for (var i=0; i<listIdBoatsPlaced.length; i++){
			document.getElementById(listIdBoatsPlaced[i]).style.backgroundColor = 'green';
		}
	}
}


//In order to have only vertical or horizontal checked when choosing boats positions
$('input[type="checkbox"]').on('change', function() {
	   $('input[type="checkbox"]').not(this).prop('checked', false);
	});

//place boats at the beginning
id("next").addEventListener("click", function(){
	//var column = parseInt(id("columnChoiceInput").value); 
	//var line = parseInt(id("lineChoiceInput").value);
	choose1BoatPosition(columnChoice, lineChoice); 
	var direction; 
	var size = boats[boatsPlaced];
	boatsPlaced ++;
	if (boatsPlaced ==1){
		getHTMLCollectionelements(); 
	}
	if (id("verticalCheckbox").checked){
		direction = 0; 
	}
	else if (id("verticalCheckbox").checked){
		direction = 1; 
	}
	//Player did not choose the direction
	else {
		direction = 2; 
	}
	var column = parseInt(columnChoice); 
	var line = parseInt(lineChoice);
	updatePlaceBoard(column, line, direction, size);
	console.log("next event");
	createMoovingDiv(1);
	
	if (boatsPlaced != (boats.length)){
		id("p1").innerHTML = "Votre bateau n°"+ (boatsPlaced + 1) +" a une longueur de <b style='color:red'>"+boats[boatsPlaced]+"</b> cases";
	}
	id("horizontalCheckbox").checked = true; 
	id("verticalCheckbox").checked = false; 
	
})

function updatePlaceBoard(column, line, direction, size){
	//vertical
	if (direction == 0){
		for (var i=0; i<size; i++){
			id("yourCell-"+(line+i)+"-"+column+"").style.backgroundColor = 'green';
			id("init-"+(line+i)+"-"+column+"").style.backgroundColor = 'green';
			listIdBoatsPlaced.push("init-"+(line+i)+"-"+column+"");
		}
	}
	//horizontal
	else {
		for (var j=0; j<size; j++){
			id("yourCell-"+line+"-"+(column+j)+"").style.backgroundColor = 'green';
			id("init-"+line+"-"+(column+j)+"").style.backgroundColor = 'green';
			listIdBoatsPlaced.push("init-"+line+"-"+(column+j)+"");
		}
	}
}

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
		console.log("choose boats")
		webSocket.send(data);
//		id("columnInput").value = ""; 
//		id("lineInput").value = "";
	}
	//id("columnChoiceInput").value = "";
	//id("lineChoiceInput").value = "";
	//id("verticalCheckbox").prop('checked', false);
	//id("horizontalCheckbox").prop('checked', false);
}







