package com.example.model

import io.micronaut.data.annotation.*
import io.micronaut.data.model.DataType
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import jakarta.persistence.JoinTable
import jakarta.persistence.JoinColumn
import com.example.model.User
import jakarta.persistence.ManyToMany

@Serdeable
@MappedEntity("tasks")
class Task {
    static final String STATUS_PENDING = "PENDING"
    static final String STATUS_IN_PROGRESS = "IN_PROGRESS"
    static final String STATUS_COMPLETED = "COMPLETED"

    @Id
    @GeneratedValue
    Long id

    @MappedProperty
    String taskName

    @MappedProperty
    Instant createdTime

    @MappedProperty
    Instant updatedTime

    @TypeDef(type = DataType.STRING)
    @MappedProperty
    String status = STATUS_PENDING

    @Relation(Relation.Kind.ONE_TO_MANY)
    Set<TaskDocument> documents = new HashSet<>()

    @MappedProperty("users")
    @Relation(value = Relation.Kind.MANY_TO_MANY, cascade = Relation.Cascade.ALL)
    @JoinTable(
        name = "task_users",
        joinColumns = @JoinColumn(name = "task_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> users = new HashSet<>()

    Task() {
        this.createdTime = Instant.now()
        this.updatedTime = this.createdTime
    }

    String getTaskName() {
        return taskName
    }

    void setTaskName(String taskName) {
        this.taskName = taskName
    }

    String getStatus() {
        return status
    }

    void setStatus(String status) {
        if (![STATUS_PENDING, STATUS_IN_PROGRESS, STATUS_COMPLETED].contains(status)) {
            throw new IllegalArgumentException("Invalid status: $status")
        }
        this.status = status
        this.updatedTime = Instant.now()
    }

    void addDocument(TaskDocument document) {
        documents.add(document)
        document.setTask(this)
    }

    void removeDocument(TaskDocument document) {
        documents.remove(document)
        document.setTask(null)
    }

    void addUser(User user) {
        if (user != null) {
            users.add(user)
        }
    }

    void removeUser(User user) {
        if (user != null) {
            users.remove(user)
        }
    }
} 