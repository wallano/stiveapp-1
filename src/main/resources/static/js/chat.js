function consumirWatson(){
	
	
		
		var textoIngresado = $("#userTextarea").val();
	
	  	// DO POST
    	$.ajax({
    		type : "GET",
			contentType : "application/json",
			url : "/chatUser?textoIngresado="+textoIngresado,
			dataType : 'json',
			success : function(result) {
				if(result.status == "Done"){
					$("<div></div>").attr('class','row flex-nowrap message-row user p-4')
					.append("<img class='avatar mr-4' src='/images/avatars/profile.jpg' alt='John Doe'/>")
					.append($("<div class='bubble'></div>").append("<div class='message'>"+result.data.textoUsuario+"</div>"))	
					.appendTo('#parentChat');
					
					$("<div></div>").attr('class','row flex-nowrap message-row contact p-4')
					.append("<img class='avatar mr-4' src='/images/avatars/Steve.png' alt='John Doe'/>")
					.append($("<div class='bubble'></div>").append("<div class='message'>"+result.data.textoRobot+"</div>"))	
					.appendTo('#parentChat');
					
					$("#userTextarea").val('');
					
					setTimeout(function(){
						$("#parentChat").animate({ scrollTop: $('#parentChat').prop("scrollHeight")}, 1000);
					},200);
				}else{
					//$("#postResultDiv").html("<strong>Error</strong>");
				}
				console.log(result);
			},
			error : function(e) {
				alert("Error!")
				console.log("ERROR: ", e);
			}
    	});
}

