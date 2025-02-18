package com.example.auth

import com.example.service.AuthenticationService
import com.example.repository.UserRepository
import io.micronaut.core.annotation.Nullable
import io.micronaut.security.authentication.*
import jakarta.inject.Singleton
import org.reactivestreams.Publisher
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.inject.Inject

@Slf4j
@CompileStatic
@Singleton
class AuthenticationProviderUserPassword implements AuthenticationProvider {
    
    @Inject
    AuthenticationService authenticationService
    
    @Inject
    UserRepository userRepository
    
    @Override
    Publisher<AuthenticationResponse> authenticate(@Nullable Object requestContext, 
                                                AuthenticationRequest authenticationRequest) {
        return Flux.<AuthenticationResponse>create({ emitter ->
            String username = authenticationRequest.identity as String
            String password = authenticationRequest.secret as String
            
            def userOptional = userRepository.findByUsername(username)
            
            if (userOptional.present && authenticationService.authenticate(username, password)) {
                def user = userOptional.get()
                if (!user.enabled) {
                    emitter.next(AuthenticationResponse.failure("Account is disabled"))
                } else {
                    Map<String, Object> attributes = [
                        'sub': username,
                        'roles': user.roles.toList(),
                        'username': username
                    ]
                    emitter.next(AuthenticationResponse.success(username, user.roles.toList(), attributes))
                }
            } else {
                emitter.next(AuthenticationResponse.failure("Invalid credentials"))
            }
            emitter.complete()
        }, FluxSink.OverflowStrategy.ERROR)
    }
} 