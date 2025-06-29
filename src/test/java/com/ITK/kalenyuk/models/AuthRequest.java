package com.ITK.kalenyuk.models;


import lombok.AllArgsConstructor;
import lombok.Data;


@Data

public class AuthRequest {
    private int id;
    private String login;
    private String password;

    public AuthRequest(String login, String password){
        this.login = login;
        this.password = password;
    }

}
