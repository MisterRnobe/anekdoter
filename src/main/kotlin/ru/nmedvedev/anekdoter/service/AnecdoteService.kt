package ru.nmedvedev.anekdoter.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.nmedvedev.anekdoter.model.Rate
import ru.nmedvedev.anekdoter.repository.AnecdoteRepository
import ru.nmedvedev.anekdoter.repository.RateRepository
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import ru.nmedvedev.anekdoter.model.Anecdote as AnecdoteDb

private val log = KotlinLogging.logger {}

@Component
class AnecdoteService(
    private val anecdoteGenerator: AnecdoteGenerator,
    private val anecdoteRepository: AnecdoteRepository,
    private val rateRepository: RateRepository,
) {
    fun suggestAnecdote(sessionId: String): Anecdote {
        val current = anecdoteRepository.findOneUnratedWithRating(sessionId)

        if (current == null) {
            val saved = anecdoteRepository.save(
                AnecdoteDb(id = UUID.randomUUID(), text = anecdoteGenerator.generate())
            )
            return Anecdote(saved.id!!, saved.text!!, BigDecimal.ZERO, 0)
        }

        val id = current.getId()
        val sumByAnecdoteId = current.getRatingSum() ?: 0L
        val ratingCount = current.getRatingCount() ?: 0
        return Anecdote(
            id = id,
            text = current.getText(),
            rating = if (sumByAnecdoteId == 0L) {
                BigDecimal.ZERO
            } else {
                BigDecimal(sumByAnecdoteId).divide(BigDecimal(ratingCount), 2, RoundingMode.HALF_UP).stripTrailingZeros()
            },
            ratingCount = ratingCount,
        )
    }

    @Transactional
    fun rate(sessionId: String, id: UUID, rate: Int) {
        rateRepository.save(
            Rate(
                id = UUID.randomUUID(),
                sessionId = sessionId,
                rate = rate,
                anecdote = anecdoteRepository.findById(id).orElseThrow()
            )
        )
    }
}

data class Anecdote(
    val id: UUID,
    val text: String,
    val rating: BigDecimal,
    val ratingCount: Int,
)