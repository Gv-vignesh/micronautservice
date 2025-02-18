package com.example.config

import io.micronaut.context.annotation.Requires
import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.transaction.annotation.Transactional
import jakarta.inject.Singleton
import liquibase.Liquibase
import liquibase.database.Database
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.sql.DataSource

@Singleton
@Requires(beans = DataSource)
class LiquibaseConfig {
    private static final Logger LOG = LoggerFactory.getLogger(LiquibaseConfig.class)
    private final DataSource dataSource

    LiquibaseConfig(DataSource dataSource) {
        this.dataSource = dataSource
    }

    @EventListener
    @Transactional
    void onStartup(StartupEvent event) {
        LOG.info("Running Liquibase migrations...")
        def connection = null
        def database = null
        try {
            connection = dataSource.connection
            def jdbcConnection = new JdbcConnection(connection)
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection)
            def resourceAccessor = new ClassLoaderResourceAccessor(this.class.classLoader)
            def liquibase = new Liquibase("db/changelog/db.changelog-master.xml", resourceAccessor, database)
            
            liquibase.update("")
            LOG.info("Liquibase migrations completed successfully")
        } catch (Exception e) {
            LOG.error("Error running Liquibase migrations", e)
            throw e
        } finally {
            try {
                database?.close()  // Close the database first
            } catch (Exception e) {
                LOG.warn("Error closing database", e)
            }
            try {
                connection?.close()  // Then close the connection
            } catch (Exception e) {
                LOG.warn("Error closing connection", e)
            }
        }
    }
} 