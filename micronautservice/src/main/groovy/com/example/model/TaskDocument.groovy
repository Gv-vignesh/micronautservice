package com.example.model

import io.micronaut.data.annotation.*
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("task_documents")
class TaskDocument {
    @Id
    @GeneratedValue
    UUID id

    @MappedProperty
    String documentName

    @Relation(Relation.Kind.MANY_TO_ONE)
    Task task

    @MappedProperty
    Instant createdTime

    TaskDocument() {
        this.createdTime = Instant.now()
    }

    String getDocumentName() {
        return documentName
    }

    void setDocumentName(String documentName) {
        this.documentName = documentName
    }
} 