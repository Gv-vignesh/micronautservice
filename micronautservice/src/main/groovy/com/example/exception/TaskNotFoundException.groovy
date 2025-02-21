package com.example.exception

class TaskNotFoundException extends RuntimeException {
    TaskNotFoundException(Long taskId) {
        super("Task not found with id: $taskId")
    }
} 