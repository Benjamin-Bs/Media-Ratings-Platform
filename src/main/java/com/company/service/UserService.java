package com.company.service;

import com.company.data.UserRepository;
import com.company.models.User;

import java.util.UUID;

public class UserService {

    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(String username, String password) throws Exception {

        //Get user from data layer
        User user = this.userRepository.getUserByUsername(username);

        //Check if user exist
        if (user == null) {
            throw new Exception("User not found");
        }

        //Check if password is correct
        if (!user.getPassword().equals(password)) {
            throw new Exception("Incorrect password");
        }

        //TOKEN
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        this.userRepository.updateUser(user);

        // Return Token
        return token;
    }

    public User register(String username, String password) throws Exception {
        if (this.userRepository.getUserByUsername(username) != null) {
            throw new Exception("Username already exists");
        }

        User newUser = new User(username, password);
        int createdId = userRepository.createUser(newUser);
        return this.userRepository.getUser(createdId);
    }

    public User getProfilByToken(String Token) throws Exception {
        User user = this.userRepository.getUserByToken(Token);
        if (user == null) {
            throw new Exception("User not found");
        }
        return user;
    }

}
