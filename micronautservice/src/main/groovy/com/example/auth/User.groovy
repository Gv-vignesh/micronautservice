package com.example.auth

import io.micronaut.serde.annotation.Serdeable

@Serdeable
class User {
    String username
    String password
    List<String> roles = []
} 