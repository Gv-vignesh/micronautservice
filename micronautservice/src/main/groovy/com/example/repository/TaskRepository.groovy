package com.example.repository

import com.example.model.Task
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository

@JdbcRepository(dialect = Dialect.POSTGRES)
interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findByUsersId(Long userId)
    List<Task> findByStatus(String status)
} 