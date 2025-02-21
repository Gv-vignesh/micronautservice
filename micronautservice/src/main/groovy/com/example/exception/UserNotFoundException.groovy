package com.example.exception

class UserNotFoundException extends RuntimeException {
    UserNotFoundException(Long userId) {
        super("User not found with id: $userId")
    }
} 