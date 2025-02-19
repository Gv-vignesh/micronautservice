package com.example.repository

import com.example.model.TaskDocument
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TaskDocumentRepository extends CrudRepository<TaskDocument, UUID> {
    List<TaskDocument> findByTaskId(Long taskId)
} 