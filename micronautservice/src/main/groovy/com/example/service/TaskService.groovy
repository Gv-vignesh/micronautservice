package com.example.service

import com.example.model.Task
import com.example.model.TaskDocument
import com.example.model.User
import com.example.repository.TaskRepository
import com.example.repository.TaskDocumentRepository
import com.example.repository.UserRepository
import jakarta.inject.Singleton
import jakarta.inject.Inject
import groovy.transform.CompileStatic
import com.example.exception.TaskNotFoundException
import com.example.exception.UserNotFoundException
import jakarta.transaction.Transactional
import java.time.Instant
import groovy.util.logging.Slf4j

@Slf4j
@Singleton
@CompileStatic
class TaskService {
    private final TaskRepository taskRepository
    private final TaskDocumentRepository taskDocumentRepository
    private final UserRepository userRepository

    @Inject
    TaskService(
        TaskRepository taskRepository, 
        TaskDocumentRepository taskDocumentRepository,
        UserRepository userRepository
    ) {
        this.taskRepository = taskRepository
        this.taskDocumentRepository = taskDocumentRepository
        this.userRepository = userRepository
    }

    @Transactional
    Task createTask(String taskName, List<Long> userIds) {
        log.info("Creating task with name: {} and userIds: {}", taskName, userIds)
        
        def task = new Task(
            taskName: taskName,
            status: Task.STATUS_PENDING,
            createdTime: Instant.now(),
            updatedTime: Instant.now()
        )

        // Add users before saving
        userIds.each { userId ->
            log.info("Finding user with id: {}", userId)
            def user = userRepository.findById(userId)
                .orElseThrow({ new UserNotFoundException(userId) })
            log.info("Found user: {} (id: {})", user.username, user.id)
            
            task.addUser(user)
            log.info("Added user {} to task", user.id)
        }

        // Save task with users
        log.info("Saving task with users")
        def savedTask = taskRepository.save(task)
        log.info("Task saved with id: {} and users count: {}", savedTask.id, savedTask.users?.size())
        
        return savedTask
    }

    TaskDocument addDocumentToTask(Long taskId, String documentName) {
        def task = taskRepository.findById(taskId)
            .orElseThrow({ new TaskNotFoundException(taskId) })

        def document = new TaskDocument()
        document.documentName = documentName
        document.task = task

        return taskDocumentRepository.save(document)
    }

    Task getTask(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow({ new TaskNotFoundException(taskId) })
    }

    List<Task> getTasksByUser(Long userId) {
        return taskRepository.findByUsersId(userId)
    }
} 