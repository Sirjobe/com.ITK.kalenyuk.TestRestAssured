package com.ITK.kalenyuk.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthRequest {
    @NonNull
    private String token;



}