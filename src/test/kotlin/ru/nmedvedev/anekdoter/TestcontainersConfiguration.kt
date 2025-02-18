package ru.nmedvedev.anekdoter

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    fun <E: PostgreSQLContainer<E>> postgresqlContainer(): PostgreSQLContainer<E> {
        return PostgreSQLContainer<E>(DockerImageName.parse("postgres:alpine")).also {
            it.start()
            Thread.sleep(5000)
        }
    }
}
