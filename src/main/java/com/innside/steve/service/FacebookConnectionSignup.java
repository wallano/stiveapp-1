package com.innside.steve.service;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

import com.innside.steve.model.User;
import com.innside.steve.repository.UserRepository;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
	private UserService userService;

    @Override
    public String execute(Connection<?> connection) {
        System.out.println("signup === ");
        final User user = new User();
        User userDatabase = null;
        user.setEmail(connection.getDisplayName());
        user.setPassword(randomAlphabetic(8));
        user.setName(connection.getDisplayName());
        userDatabase = userService.findUserByEmail(connection.getDisplayName());
        if(userDatabase == null){
        	userRepository.save(user);
        }
        
        return user.getEmail();
    }

}
