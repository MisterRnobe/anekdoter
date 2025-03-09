package ru.nmedvedev.anekdoter.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.client.TelegramClient
import ru.nmedvedev.anekdoter.repository.AnecdoteRepository
import java.time.Clock
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference

@Component
@ConditionalOnProperty(
    value = ["anekdoter.background.telegram-poster.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class TelegramPoster(
    private val clock: Clock,
    private val telegramClient: TelegramClient,
    private val anecdoteRepository: AnecdoteRepository,
) {

    private val lastExecutionTime: AtomicReference<Instant> = AtomicReference(clock.instant())

    @Scheduled(cron = "0 */5 * * * *", scheduler = "taskScheduler")
    fun postBestAnecdote() {
        val dateFrom = lastExecutionTime.get()
        logger.info { "Going to post best anecdote: $dateFrom" }
        val anecdote = anecdoteRepository.findTopRatedFrom(dateFrom)
        if (anecdote == null) {
            logger.info { "No anecdotes..." }
            return
        }

        val tags = anecdote.tags!!.joinToString(", ") { "#${it.name!!.replace(" ", "_")}" }
        val message = MESSAGE_TEMPLATE.format(
            anecdote.text!!,
            tags,
            anecdote.createdBy!!,
        )
        logger.info { "Posting message: $message" }
        telegramClient.postMessage(message).get()
        lastExecutionTime.set(clock.instant())
    }
}

private val logger = KotlinLogging.logger {}

private const val MESSAGE_TEMPLATE = "%s\n\nтеги: %s\n\nавтор: %s"