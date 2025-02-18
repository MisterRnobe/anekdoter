package ru.nmedvedev.anekdoter

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import ru.nmedvedev.anekdoter.repository.AnecdoteRepository
import ru.nmedvedev.anekdoter.service.AnecdoteGenerator
import ru.nmedvedev.anekdoter.service.AnecdoteService

@Import(TestcontainersConfiguration::class)
@SpringBootTest
abstract class BaseTest {

    @Autowired
    protected lateinit var anecdoteService: AnecdoteService

    @MockitoSpyBean
    protected lateinit var anecdoteGenerator: AnecdoteGenerator

    @Autowired
    protected lateinit var anecdoteRepository: AnecdoteRepository

    @AfterEach
    fun tearDown() {
        anecdoteRepository.deleteAll()
    }
}