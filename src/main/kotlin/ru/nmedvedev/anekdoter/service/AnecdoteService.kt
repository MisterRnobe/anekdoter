package ru.nmedvedev.anekdoter.service

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.nmedvedev.anekdoter.model.Rate
import ru.nmedvedev.anekdoter.repository.AnecdoteRepository
import ru.nmedvedev.anekdoter.repository.RateRepository
import java.util.UUID
import ru.nmedvedev.anekdoter.model.Anecdote as AnecdoteDb

@Component
class AnecdoteService(
    private val anecdoteGenerator: AnecdoteGenerator,
    private val anecdoteRepository: AnecdoteRepository,
    private val rateRepository: RateRepository,
) {
    fun suggestAnecdote(sessionId: String): Anecdote {
        val anecdote = anecdoteRepository.findOneUnrated(sessionId) ?: anecdoteRepository.save(
            AnecdoteDb(id = UUID.randomUUID(), text = anecdoteGenerator.generate())
        )
        return Anecdote(anecdote.id!!, anecdote.text!!)
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
)