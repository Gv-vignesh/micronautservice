package com.example.controller

import com.example.service.TaskService
import com.example.exception.TaskNotFoundException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import groovy.transform.CompileStatic
import io.micronaut.serde.annotation.Serdeable

@CompileStatic
@Controller("/api/tasks")
@Secured(SecurityRule.IS_AUTHENTICATED)
class TaskController {
    
    private final TaskService taskService

    @Inject
    TaskController(TaskService taskService) {
        this.taskService = taskService
    }

    @Post
    HttpResponse<?> createTask(@Body CreateTaskRequest request) {
        try {
            def task = taskService.createTask(request.taskName, request.userIds)
            return HttpResponse.created([
                id: task.id,
                taskName: task.taskName,
                status: task.status,
                createdTime: task.createdTime,
                updatedTime: task.updatedTime
            ])
        } catch (Exception e) {
            return HttpResponse.badRequest([message: e.message])
        }
    }

    @Post("/{taskId}/documents")
    HttpResponse<?> addDocument(Long taskId, @Body AddDocumentRequest request) {
        try {
            def document = taskService.addDocumentToTask(taskId, request.documentName)
            return HttpResponse.created([
                id: document.id,
                documentName: document.documentName,
                createdTime: document.createdTime
            ])
        } catch (TaskNotFoundException e) {
            return HttpResponse.notFound([message: e.message])
        } catch (Exception e) {
            return HttpResponse.badRequest([message: e.message])
        }
    }

    @Get("/{taskId}")
    HttpResponse<?> getTask(Long taskId) {
        try {
            def task = taskService.getTask(taskId)
            return HttpResponse.ok([
                id: task.id,
                taskName: task.taskName,
                status: task.status,
                createdTime: task.createdTime,
                updatedTime: task.updatedTime,
                documents: task.documents.collect { doc ->
                    [
                        id: doc.id,
                        documentName: doc.documentName,
                        createdTime: doc.createdTime
                    ]
                }
            ])
        } catch (TaskNotFoundException e) {
            return HttpResponse.notFound([message: e.message])
        }
    }
}

@CompileStatic
@Serdeable
class CreateTaskRequest {
    String taskName
    List<Long> userIds
}

@CompileStatic
@Serdeable
class AddDocumentRequest {
    String documentName
} 