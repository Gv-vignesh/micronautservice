package com.example

import io.micronaut.runtime.Micronaut
import groovy.util.logging.Slf4j

@Slf4j
class Application {
    static void main(String[] args) {
        println "Starting Micronaut application..."
        def app = Micronaut.run(Application, args)
        println "Micronaut application started on port: ${app.environment.getProperty('micronaut.server.port', Integer, 8080)}"
    }
} 