package com.innside.steve.service;

import com.innside.steve.model.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
}
