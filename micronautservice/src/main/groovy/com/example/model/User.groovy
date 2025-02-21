package com.example.model

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.TypeDef
import io.micronaut.data.annotation.Relation
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.data.model.DataType
import io.micronaut.serde.annotation.Serdeable
import com.example.model.Task
import jakarta.persistence.*
import java.time.Instant
import jakarta.persistence.JoinTable
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToMany

@MappedEntity("users")
@Serdeable
class User {
    @Id
    @GeneratedValue
    Long id
    
    String username
    String password
    String profileImage
    
    @TypeDef(type = DataType.STRING_ARRAY)
    String[] roles
    
    Boolean enabled
    
    Date createdAt
    Date updatedAt
    
    @MappedProperty("tasks")
    @Relation(value = Relation.Kind.MANY_TO_MANY, mappedBy = "users", cascade = Relation.Cascade.ALL)
    Set<Task> tasks = new HashSet<>()
    
    User() {
        this.createdAt = new Date()
        this.updatedAt = new Date()
    }

    User(String username, String password) {
        this.username = username
        this.password = password
        this.createdAt = new Date()
        this.updatedAt = new Date()
    }
    
    // Getters and setters
    Long getId() {
        return id
    }
    
    void setId(Long id) {
        this.id = id
    }
    
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
    
    String[] getRoles() {
        return roles
    }
    
    void setRoles(String[] roles) {
        this.roles = roles
    }
    
    Boolean getEnabled() {
        return enabled
    }
    
    void setEnabled(Boolean enabled) {
        this.enabled = enabled
    }

    Date getCreatedAt() {
        return createdAt
    }

    void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt
    }

    Date getUpdatedAt() {
        return updatedAt
    }

    void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt
    }

    void addTask(Task task) {
        if (task != null) {
            tasks.add(task)
        }
    }

    void removeTask(Task task) {
        if (task != null) {
            tasks.remove(task)
        }
    }
} 