package com.example.controller

import com.example.model.User
import com.example.service.AuthenticationService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject

@Controller("/api")
@Secured(SecurityRule.IS_AUTHENTICATED)
class AuthenticationController {
    
    @Inject
    AuthenticationService authenticationService

    @Get("/users/{username}")
    HttpResponse<?> getUserData(String username) {
        try {
            User user = authenticationService.findByUsername(username)
            if (!user) {
                return HttpResponse.notFound()
            }
            
            return HttpResponse.ok([
                username: user.username,
                profileImage: user.profileImage
            ])
        } catch (Exception e) {
            return HttpResponse.badRequest([
                message: "Error fetching user data: ${e.message}"
            ])
        }
    }
} 