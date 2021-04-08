$(function() {
	horizontalDrag();
});

var sizeBoat = 2;
var boardHorizontalSize = 3; 
var boardVerticalSize = 3; 

//O=Vertical, 1=Horizontal
function unableDroppable(sizeBoat, direction) {
	if (direction == 0){
		for (var i=0; i<boardVerticalSize; i++){
			id("id-"+(boardVerticalSize-1)+"-"+i).className = "cellNoDroppable"
		}
		for (var j=0; j<(boardVerticalSize-sizeBoat+1); j++){
			for (var k=0; k<boardHorizontalSize; k++){
				id("id-"+j+"-"+k).className = "cell ui-droppable"
			}
		}
	}
	else if (direction ==1){
		for (var i=0; i<boardHorizontalSize; i++){
			id("id-"+i+"-"+(boardHorizontalSize-1)).className = "cellNoDroppable"
		}
		for (var j=0; j<(boardVerticalSize); j++){
			for (var k=0; k<(boardHorizontalSize-sizeBoat+1); k++){
				id("id-"+j+"-"+k).className = "cell ui-droppable"
			}
		}
	}
	else {
		console.log("Not good direction"); 
	}
}

function horizontalDrag() {
	unableDroppable(sizeBoat, 1); 
	$("#moovingDiv").draggable({
		revert: "invalid", 
		cursor: "move", 
		cursorAt: { top: 25, left: 25 }
	});
	$(".cell").droppable({
		tolerance: "pointer",
		drop: function(event, ui) {
			var $this = $(this);
			console.log($(this).get(0).id)
			$(this).css("background-color","green");
			tolerance: "pointer",
		    ui.draggable.position({
		    	my: "left-25%",
		    	at: "center",
		    	of: $this,
		    	using: function(pos) {
		    		$(this).animate(pos, 200, "linear");
		    	}
		    });
		 }
	});
}

function verticalDrag() {
	unableDroppable(sizeBoat, 0);
	$("#moovingDiv").draggable({
		revert: "invalid", 
		cursor: "move", 
		cursorAt: { top: 25, left: 25 }
	});
	$(".cell").droppable({
		tolerance: "pointer",
		drop: function(event, ui) {
			var $this = $(this);
			console.log($(this).get(0).id)
			$(this).css("background-color","green");
			tolerance: "pointer",
		    ui.draggable.position({
		    	my: "top-25%",
		    	at: "center",
		    	of: $this,
		    	using: function(pos) {
		    		$(this).animate(pos, 200, "linear");
		    	}
		    });
		 }
	});
}

$('input[type="checkbox"]').on('change', function() {
	$('input[type="checkbox"]').not(this).prop('checked', false);
});

//$('#horizontalCheckbox').checked(function() {
//	console.log("in");
//	$("#container").innerHTML = "<div id='moovingDiv' style='display:flex; flex-direction : horizontal; width :100px'> +" +
//			"<div id='moovingCell' class='cellTest'></div><div class='cellTest'></div></div>"
//})

function testHorizontal() {
	id("container").innerHTML = "<div id='moovingDiv' style='display:flex; flex-direction : horizontal; width :100px'>" +
			"<div id='moovingCell' class='cellNoDroppable'></div><div class='cellNoDroppable'></div></div>";
	console.log("1")
	horizontalDrag();
}

function testVertical() {
	id("container").innerHTML = "<div id='moovingDiv' style='width :50px; height: auto'> <div id='moovingCell' class='cellNoDroppable'></div>" +
			"<div style='margin-top: -6px' class='cellNoDroppable'></div></div>"
	verticalDrag();
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}
