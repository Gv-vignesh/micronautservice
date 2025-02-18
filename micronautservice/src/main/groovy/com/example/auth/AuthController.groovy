package com.example.auth

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.security.authentication.UsernamePasswordCredentials
import io.micronaut.security.authentication.AuthenticationProvider
import io.micronaut.security.authentication.AuthenticationRequest
import io.micronaut.security.authentication.AuthenticationResponse
import io.micronaut.http.MediaType
import jakarta.inject.Inject
import io.micronaut.security.token.generator.TokenGenerator
import reactor.core.publisher.Mono
import groovy.util.logging.Slf4j
import io.micronaut.serde.annotation.Serdeable
import groovy.transform.CompileStatic
import java.util.Optional
import io.micronaut.security.authentication.Authentication
import java.util.function.Function

@Slf4j
@CompileStatic
@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
class AuthController {
    
    private final AuthenticationProvider authenticationProvider
    private final TokenGenerator tokenGenerator
    
    @Inject
    AuthController(AuthenticationProvider authenticationProvider, TokenGenerator tokenGenerator) {
        this.authenticationProvider = authenticationProvider
        this.tokenGenerator = tokenGenerator
    }
    
    @Post(value = "/login", produces = MediaType.APPLICATION_JSON)
    Mono<MutableHttpResponse<?>> login(@Body User user) {
        log.info("Login attempt for user: ${user.username}")
        
        return Mono.from(authenticationProvider.authenticate(null, 
            new UsernamePasswordCredentials(user.username, user.password)))
            .flatMap { AuthenticationResponse response ->
                if (!response.isAuthenticated()) {
                    log.error("Authentication failed")
                    return Mono.just(HttpResponse.unauthorized())
                }

                Authentication auth = response.getAuthentication()
                    .orElseThrow({ new RuntimeException("No authentication present") })
                
                Map<String, Object> attributes = auth.getAttributes()
                
                Optional<String> token = tokenGenerator.generateToken(attributes)
                if (!token.present) {
                    log.error("Failed to generate token")
                    return Mono.just(HttpResponse.serverError())
                }

                log.info("Authentication successful")
                return Mono.just(HttpResponse.ok(new LoginResponse(token.get())))
            }
            .onErrorResume { Throwable throwable ->
                log.error("Authentication error", throwable)
                return Mono.just(HttpResponse.unauthorized())
            }
    }
} 