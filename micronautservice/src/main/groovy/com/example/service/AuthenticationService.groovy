package com.example.service

import com.example.model.User
import com.example.repository.UserRepository
import jakarta.inject.Singleton
import org.mindrot.jbcrypt.BCrypt
import jakarta.inject.Inject
import java.util.Base64
import io.micronaut.json.JsonMapper

@Singleton
class AuthenticationService {
    @Inject
    UserRepository userRepository
    
    @Inject
    JsonMapper jsonMapper
    
    User registerUser(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        User user = new User(
            username: username, 
            password: hashedPassword,
            enabled: true,
            roles: ["ROLE_USER"] as String[],
            createdAt: new Date(),
            updatedAt: new Date()
        )
        return userRepository.save(user)
    }
    
    boolean authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username)
        if (!userOptional.present) {
            return false
        }
        
        User user = userOptional.get()
        return BCrypt.checkpw(password, user.password)
    }

    void updateProfileImage(String username, String imageUrl) {
        userRepository.updateProfileImage(username, imageUrl)
    }

    User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null)
    }

    String getUsernameFromToken(String token) {
        def base64Url = token.split('\\.')[1]
        def base64 = base64Url.replace('-', '+').replace('_', '/')
        def payload = new String(Base64.decoder.decode(base64))
        def claims = jsonMapper.readValue(payload, Map.class)
        return claims.sub
    }
} 