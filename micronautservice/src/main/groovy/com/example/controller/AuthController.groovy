package com.example.controller

import com.example.service.AuthenticationService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.security.token.generator.TokenGenerator
import jakarta.inject.Inject
import io.micronaut.security.authentication.*
import reactor.core.publisher.Mono
import groovy.util.logging.Slf4j
import groovy.transform.CompileStatic

@Slf4j
@CompileStatic
@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
class AuthController {
    
    private final AuthenticationProvider authenticationProvider
    private final TokenGenerator tokenGenerator
    private final AuthenticationService authenticationService
    
    @Inject
    AuthController(AuthenticationProvider authenticationProvider, 
                  TokenGenerator tokenGenerator,
                  AuthenticationService authenticationService) {
        this.authenticationProvider = authenticationProvider
        this.tokenGenerator = tokenGenerator
        this.authenticationService = authenticationService
    }
    
    @Post("/register")
    HttpResponse<?> register(@Body RegisterRequest request) {
        try {
            def user = authenticationService.registerUser(request.username, request.password)
            return HttpResponse.created(["id": user.id, "username": user.username])
        } catch (Exception e) {
            return HttpResponse.badRequest([message: "Registration failed: ${e.message}"])
        }
    }
    
    // @Post(value = "/login")
    // Mono<MutableHttpResponse<?>> login(@Body LoginRequest credentials) {
    //     log.info("Login attempt for user: ${credentials.username}")
        
    //     return Mono.from(authenticationProvider.authenticate(null, 
    //         new UsernamePasswordCredentials(credentials.username, credentials.password)))
    //         .map { AuthenticationResponse response ->
    //             if (!response.authenticated) {
    //                 return HttpResponse.unauthorized()
    //             }

    //             def auth = response.authentication
    //                 .orElseThrow({ new RuntimeException("No authentication present") })
                
    //             def token = tokenGenerator.generateToken(auth.attributes)
    //                 .orElseThrow({ new RuntimeException("Failed to generate token") })

    //             return HttpResponse.ok([
    //                 "token": token,
    //                 "username": credentials.username
    //             ])
    //         }
    //         .onErrorResume { Throwable throwable ->
    //             log.error("Authentication error", throwable)
    //             return Mono.just(HttpResponse.unauthorized())
    //         }
    // }
}
@CompileStatic
class LoginRequest {
    String username
    String password
}

@CompileStatic
class RegisterRequest {
    String username
    String password
}
