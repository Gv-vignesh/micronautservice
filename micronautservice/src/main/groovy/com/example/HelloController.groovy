package com.example

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.security.authentication.Authentication

@Controller("/hello")
@Secured(SecurityRule.IS_AUTHENTICATED)
class HelloController {
    
    @Get("/")
    String index(Authentication authentication) {
        return "Hello, ${authentication.name}!"
    }
} 