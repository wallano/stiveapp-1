package com.innside.steve.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.innside.steve.ajax.response.Response;
import com.innside.steve.chatbot.WcsClient;
import com.innside.steve.model.Chatbot;

@RestController
public class ChatController {
	
	String wcsClientId = "";
	WcsClient watson = null;
	String saludoWatson = "";
	
	@RequestMapping(value="/chatUser", method = RequestMethod.GET)
	public Response chatbotLive(@RequestParam(value = "textoIngresado", required = true) String textoIngresado,
			HttpServletRequest request){
		WcsClient watson = (WcsClient) request.getSession().getAttribute("watson");
		wcsClientId = (String) request.getSession().getAttribute("wcsClientId");		
		String respuestaWatson = "";
		MessageResponse wcsRes01 = watson.sendMessage(wcsClientId, textoIngresado);
		respuestaWatson = wcsRes01.getTextConcatenated("");
		Response response = new Response();
		Chatbot chatbotResponse = new Chatbot();
		chatbotResponse.setTextoSaludo(saludoWatson);
		chatbotResponse.setTextoRobot(respuestaWatson);
		chatbotResponse.setTextoUsuario(textoIngresado);
		response.setData(chatbotResponse);
		response.setStatus("Done");
		return response;
	}

}
