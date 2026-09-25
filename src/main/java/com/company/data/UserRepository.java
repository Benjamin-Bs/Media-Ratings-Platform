package com.company.data;

import com.company.models.User;

public interface UserRepository {

    //CRUD: Create, Read, Update, Delete

     int createUser(User user);

     User getUser(int userId);

     User getUserByUsername(String username);

     User getUserByToken(String token);

     User updateUser(User user);

     void deleteUser(int userId);

}
