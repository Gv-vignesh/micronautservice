package com.example.auth

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class LoginResponse {
    String token
    
    LoginResponse(String token) {
        this.token = token
    }
} 