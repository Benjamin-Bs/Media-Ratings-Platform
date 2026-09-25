package com.company.data;

import com.company.models.User;

import java.util.ArrayList;
import java.util.List;

public class InMemoryUserRepository implements UserRepository {


    private static int nextId = 1;

    List<User> users = new ArrayList<>();

    @Override
    public int createUser(User user) {
        user.setId(nextId);
        nextId++;
        users.add(user);
        return user.getId();
    }

    @Override
    public User getUser(int userId) {

        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User updateUser(User user) {

        if (user == null) return null;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return user;
            }
        }
        return null;
    }

    @Override
    public void deleteUser(int userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == userId) {
                users.remove(i);
                break;
            }
        }
    }

    @Override
    public User getUserByUsername(String username) {
        if (username == null) return null;
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User getUserByToken(String token) {
        if (token == null) return null;
        for (User user : users) {
            if (token.equals(user.getToken())) {
                return user;
            }
        }

        return null;
    }

}
