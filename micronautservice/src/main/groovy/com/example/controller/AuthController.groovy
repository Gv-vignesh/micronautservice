package com.example.controller

import com.example.service.AuthenticationService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import io.micronaut.core.annotation.Introspected
import io.micronaut.serde.annotation.Serdeable

@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
class AuthController {
    @Inject
    AuthenticationService authenticationService
    
    @Post("/register")
    HttpResponse register(@Body RegisterRequest request) {
        try {
            def user = authenticationService.registerUser(request.username, request.password)
            return HttpResponse.created(["id": user.id, "username": user.username])
        } catch (Exception e) {
            return HttpResponse.badRequest([message: "Registration failed: ${e.message}"])
        }
    }
}

@Introspected
@Serdeable
class RegisterRequest {
    String username
    String password
    
    // Getters and setters
    String getUsername() {
        return username
    }
    
    void setUsername(String username) {
        this.username = username
    }
    
    String getPassword() {
        return password
    }
    
    void setPassword(String password) {
        this.password = password
    }
}