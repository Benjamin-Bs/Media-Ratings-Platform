package com.company.models;

import com.company.Interfaces.IIdentifiable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class User implements IIdentifiable {


    @Setter
    private int id;
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private List<Integer> favMediaIds;

    public User(String username, String password) {
        this.username = username;
        this.password = password;

        this.favMediaIds = new ArrayList<>();
    }



    @Override
    public int getId() {
        return id;
    }

}
