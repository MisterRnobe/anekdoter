package ru.nmedvedev.anekdoter.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.repository.TagRepository
import ru.nmedvedev.anekdoter.service.event.AnecdoteCreatedEvent
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService

private const val FAKE_SESSION_ID = "fake_session"

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty(
    value = ["anekdoter.background.generation.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class BackgroundGenerator(
    private val chatBotRater: ChatBotRater,
    private val tagRepository: TagRepository,
    private val anecdoteService: AnecdoteService,
    private val asyncTaskExecutor: AsyncTaskExecutor,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady(event: ApplicationReadyEvent) {
        asyncTaskExecutor.submit {
            try {
                var hasTags = false
                while (!hasTags) {
                    hasTags = tagRepository.count() > 0
                }
                anecdoteService.suggestAnecdote(FAKE_SESSION_ID, null)
            } catch (e: Exception) {
                logger.error(e) { "Failed to generate anecdote" }
            }
        }
    }

    @EventListener(AnecdoteCreatedEvent::class)
    fun onAnecdoteCrated(event: AnecdoteCreatedEvent) {
        logger.info { "got anecdote created event with anecdote_id = ${event.anecdote.id}" }
        asyncTaskExecutor.submit {
            var rate: Int? = null
            while (rate == null) {
                rate = chatBotRater.rateAnecdote(event.anecdote)?.rate
            }
            anecdoteService.rate(FAKE_SESSION_ID, event.anecdote.id!!, rate)
            anecdoteService.suggestAnecdote(FAKE_SESSION_ID, null)
        }
    }
}