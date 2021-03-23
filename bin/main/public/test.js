$(function() {
	$("#moovingDiv").draggable({cursor: "move", cursorAt: { top: 25, left: 25 }});
	$(".cell").droppable({
		tolerance: "pointer",
		drop: function(event, ui) {
			var $this = $(this);
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
});


