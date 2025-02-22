package ru.nmedvedev.anekdoter

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger {}

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun <E : PostgreSQLContainer<E>> postgresqlContainer(): PostgreSQLContainer<E> {
        return PostgreSQLContainer<E>(DockerImageName.parse("postgres:alpine")).also {
            it.start()
            Thread.sleep(3000)
            logger.info { "jdbc:postgresql://${it.username}:${it.password}@${it.host}:${it.firstMappedPort}/" }
        }
    }
}
