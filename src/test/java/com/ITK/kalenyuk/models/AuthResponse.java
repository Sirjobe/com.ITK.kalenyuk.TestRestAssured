package com.ITK.kalenyuk.models;


import lombok.Data;


@Data
public class AuthResponse {
    private String token;
    private String status;
    private String message;
}
