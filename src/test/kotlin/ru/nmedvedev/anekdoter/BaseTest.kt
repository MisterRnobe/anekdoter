package ru.nmedvedev.anekdoter

import jakarta.persistence.EntityManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import ru.nmedvedev.anekdoter.model.Tag
import ru.nmedvedev.anekdoter.repository.AnecdoteRepository
import ru.nmedvedev.anekdoter.repository.TagRepository
import ru.nmedvedev.anekdoter.service.AnecdoteGenerator
import ru.nmedvedev.anekdoter.service.AnecdoteMeta
import ru.nmedvedev.anekdoter.service.AnecdoteService
import java.util.UUID

@Import(TestcontainersConfiguration::class)
@SpringBootTest(
    properties = [
        "anekdoter.background.generation.enabled=false",
        "anekdoter.background.chatbots-rater.enabled=false",
        "anekdoter.background.telegram-poster.enabled=false",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.show-sql=true",
    ]
)
abstract class BaseTest {

    @Autowired
    protected lateinit var anecdoteService: AnecdoteService

    @MockitoSpyBean
    protected lateinit var anecdoteGenerator: AnecdoteGenerator

    @Autowired
    protected lateinit var anecdoteRepository: AnecdoteRepository

    @Autowired
    protected lateinit var tagRepository: TagRepository

    @BeforeEach
    fun beforeEach() {
        anecdoteRepository.deleteAll()
        tagRepository.deleteAll()
        tagRepository.save(Tag(name = UUID.randomUUID().toString()))
    }
}

fun anecdoteMeta(text: String = UUID.randomUUID().toString(), model: String = UUID.randomUUID().toString()) =
    AnecdoteMeta(text, model)

fun randomString() = UUID.randomUUID().toString()