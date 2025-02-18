package com.example.repository

import com.example.model.User
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import io.micronaut.data.annotation.Query

@JdbcRepository(dialect = Dialect.POSTGRES)
interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username)
    
    @Query("UPDATE users SET profile_image = :imageUrl, updated_at = CURRENT_TIMESTAMP WHERE username = :username")
    void updateProfileImage(String username, String imageUrl)
} 