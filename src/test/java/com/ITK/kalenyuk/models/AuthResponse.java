package com.ITK.kalenyuk.models;

import lombok.Data;

@Data
public class AuthResponse {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String error;
    private String error_description;
}