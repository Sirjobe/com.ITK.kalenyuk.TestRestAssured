package com.ITK.kalenyuk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {
    private String id;
    private String name;
    @JsonProperty("guest")
    private boolean isGuest;
}