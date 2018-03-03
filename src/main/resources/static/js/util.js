function cambiarLenguaje(pagina, lenguaje){
	
	if(pagina == 'login'){
		window.location.replace('login?lang=' + lenguaje);
	}
	
	if(pagina == 'register'){
		window.location.replace('register?lang=' + lenguaje);
	}
	
}