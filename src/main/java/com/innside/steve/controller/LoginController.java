package com.innside.steve.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.innside.steve.chatbot.WcsClient;
import com.innside.steve.model.Chatbot;
import com.innside.steve.model.User;
import com.innside.steve.service.UserService;
import com.mysql.jdbc.StringUtils;

@Controller
public class LoginController {
	
	private static final String WATSON_CONVERSATION_USERNAME = "7325a607-cc55-4c91-b722-a4bfc36cffdc";
    private static final String WATSON_CONVERSATION_PASSWORD = "uOCtcqSmBzak";
    private static final String WATCON_CONVERSATION_WORKSPACE_ID = "e72b084a-17d0-4f58-9da0-76b5a86722b5";
    
    String wcsClientId = "";
	WcsClient watson = null;
	String saludoWatson = "";
	
	@Autowired
	private UserService userService;

	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView index(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	@RequestMapping(value="/register", method = RequestMethod.GET)
	public ModelAndView registration(){
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("register");
		return modelAndView;
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult) {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		ObjectError erorName = new ObjectError("name","Debe ingresar el nombre");
		ObjectError erorEmail = new ObjectError("email","Debe ingresar el email");
		ObjectError erorEmailExistente = new ObjectError("email","Ya existe un usuario registrado con ese email");
		ObjectError erorPassword = new ObjectError("password","Debe ingresar el password");
		ObjectError erorConfirmPassword = new ObjectError("confirmPassword","Debe ingresar confirmar el password");
		ObjectError erorComparaPassword = new ObjectError("","Lo ingresado en los campos Password y Confirm Password no coinciden");
		
		if(StringUtils.isNullOrEmpty(user.getName())){
			bindingResult.addError(erorName);
		}
		if(StringUtils.isNullOrEmpty(user.getEmail())){
			bindingResult.addError(erorEmail);
		}
		if(StringUtils.isNullOrEmpty(user.getPassword())){
			bindingResult.addError(erorPassword);
		}
		if(StringUtils.isNullOrEmpty(user.getConfirmPassword())){
			bindingResult.addError(erorConfirmPassword);
		}
		if(!StringUtils.isNullOrEmpty(user.getPassword()) && !StringUtils.isNullOrEmpty(user.getConfirmPassword())){
			if(!user.getPassword().equals(user.getConfirmPassword())){
				bindingResult.addError(erorComparaPassword);
			}
		}
		if (userExists != null) {
			bindingResult.addError(erorEmailExistente);
		}
		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("register");
		} else {
			userService.saveUser(user);
			modelAndView.addObject("save", "success");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("register");
			
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/admin/home", method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome " + user.getName() + " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}
	
	@RequestMapping(value="/chat", method = RequestMethod.GET)
	public ModelAndView chatbot(@Valid Chatbot chatbot, HttpServletRequest request){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = null;
		if(auth.getName() instanceof String){
			user = userService.findUserByEmail(auth.getName());
			wcsClientId = user.getEmail();
		}else{
			wcsClientId = auth.getName();
		}
	
		watson = new WcsClient(
                WATSON_CONVERSATION_USERNAME,
                WATSON_CONVERSATION_PASSWORD,
                WATCON_CONVERSATION_WORKSPACE_ID);
		MessageResponse wcsWelcomeRes = watson.startConversation(wcsClientId);
		saludoWatson = wcsWelcomeRes.getTextConcatenated("");
		modelAndView.addObject("nombreUsuario", user.getName());
		modelAndView.addObject("saludoWatson", saludoWatson);
		request.getSession().setAttribute("watson", watson);
		request.getSession().setAttribute("wcsClientId", wcsClientId);
		modelAndView.setViewName("/chat");	
		
		return modelAndView;
	}
	
	
}
