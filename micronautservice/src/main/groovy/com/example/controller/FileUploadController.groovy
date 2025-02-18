package com.example.controller

import com.example.service.AuthenticationService
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.multipart.CompletedFileUpload
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Controller("/api")
@Secured(SecurityRule.IS_AUTHENTICATED)
class FileUploadController {
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/profile-images"
    
    @Inject
    AuthenticationService authenticationService

    FileUploadController() {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath()
        println "Upload directory path: ${uploadPath}"
        if (!Files.exists(uploadPath)) {
            println "Creating directory: ${uploadPath}"
            Files.createDirectories(uploadPath)
        }
    }

    @Post(value = "/upload/profile-image", consumes = MediaType.MULTIPART_FORM_DATA)
    HttpResponse<?> uploadProfileImage(
        @Part CompletedFileUpload file,
        @Header String authorization
    ) {
        try {
            // Extract username from JWT token
            String token = authorization.replace("Bearer ", "")
            String username = authenticationService.getUsernameFromToken(token)

            // Generate unique filename
            String originalFilename = file.filename
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'))
            String uniqueFilename = "${username}_${System.currentTimeMillis()}${fileExtension}"
            
            // Save file to disk
            Path filePath = Paths.get(UPLOAD_DIR, uniqueFilename).toAbsolutePath()
            println "Saving file to: ${filePath}"
            println "File exists before save: ${Files.exists(filePath)}"
            Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)
            println "File exists after save: ${Files.exists(filePath)}"
            println "File size: ${Files.size(filePath)} bytes"
            
            // Save file path to database
            String fileUrl = "/api/images/${uniqueFilename}"
            authenticationService.updateProfileImage(username, fileUrl)
            
            return HttpResponse.ok([
                imageUrl: fileUrl,
                message: "Profile image updated successfully"
            ])
        } catch (Exception e) {
            return HttpResponse.badRequest([
                message: "Failed to upload image: ${e.message}"
            ])
        }
    }

    @Get("/images/{filename}")
    HttpResponse<?> getImage(String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, filename).toAbsolutePath()
            println "Attempting to read image from: ${filePath}"
            if (!Files.exists(filePath)) {
                println "File not found: ${filePath}"
                return HttpResponse.notFound()
            }
            
            println "File exists, size: ${Files.size(filePath)} bytes"
            byte[] imageBytes = Files.readAllBytes(filePath)
            println "Read ${imageBytes.length} bytes from file"
            
            String contentType = "image/jpeg"
            if (filename.toLowerCase().endsWith(".png")) {
                contentType = "image/png"
            } else if (filename.toLowerCase().endsWith(".gif")) {
                contentType = "image/gif"
            }
            
            return HttpResponse.ok(imageBytes)
                .header("Content-Type", contentType)
        } catch (Exception e) {
            println "Error serving image: ${e.message}"
            e.printStackTrace()
            return HttpResponse.badRequest()
        }
    }
} 