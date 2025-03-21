package ru.nmedvedev.anekdoter.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import ru.nmedvedev.anekdoter.model.Rate
import ru.nmedvedev.anekdoter.model.Tag
import ru.nmedvedev.anekdoter.repository.AnecdoteRepository
import ru.nmedvedev.anekdoter.repository.RateRepository
import ru.nmedvedev.anekdoter.repository.TagRepository
import ru.nmedvedev.anekdoter.service.event.AnecdoteCreatedEvent
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import ru.nmedvedev.anekdoter.model.Anecdote as AnecdoteDb

@Component
class AnecdoteService(
    private val anecdoteGenerator: AnecdoteGenerator,
    private val anecdoteRepository: AnecdoteRepository,
    private val tagRepository: TagRepository,
    private val rateRepository: RateRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    @Value("\${anekdoter.default-tags-count}")
    private val defaultTagsCount: Int,
) {
    @Transactional
    fun suggestAnecdote(sessionId: String, tagIds: List<UUID>?): Anecdote {
        val current = if (tagIds == null || tagIds.isEmpty()) {
            anecdoteRepository.findOneUnrated(sessionId)
        } else {
            anecdoteRepository.findOneUnrated(sessionId, tagIds)
        }

        if (current == null) {
            val tags = if (tagIds == null || tagIds.isEmpty()) {
                tagRepository.findAny(defaultTagsCount)
            } else {
                tagRepository.findAllById(tagIds)
            }

            val generated = anecdoteGenerator.generateFor(sessionId, tags.map { it.name!! })
            val saved = anecdoteRepository.save(
                AnecdoteDb(
                    text = generated.text,
                    createdBy = generated.model,
                    tags = tags,
                )
            )
            applicationEventPublisher.publishEvent(AnecdoteCreatedEvent(saved))

            return Anecdote(saved.id!!, saved.text!!, BigDecimal.ZERO, 0, tags.toAnecdoteTags(), generated.model)
        }

        // val id = current.getId()
        // val sumByAnecdoteId = current.getRatingSum()
        // val ratingCount = current.getRatingCount()

        val id = current.id!!
        val rate = anecdoteRepository.rateAnecdote(id)
        val sumByAnecdoteId = rate?.getRatingSum() ?: 0L
        val ratingCount = rate?.getRatingCount() ?: 0

        return Anecdote(
            id = id,
            text = current.text!!,
            rating = if (sumByAnecdoteId == 0L) {
                BigDecimal.ZERO
            } else {
                BigDecimal(sumByAnecdoteId).divide(BigDecimal(ratingCount), 2, RoundingMode.HALF_UP).stripTrailingZeros()
            },
            ratingCount = ratingCount,
            tags = current.tags!!.toAnecdoteTags(),
            author = current.createdBy!!,
        )
    }

    @Transactional
    fun rate(sessionId: String, id: UUID, rate: Int) {
        rateRepository.save(
            Rate(
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
    val tags: List<AnecdoteTag>,
    val author: String,
)

data class AnecdoteTag(
    val id: UUID,
    val name: String,
)

private fun List<Tag>.toAnecdoteTags() = map { AnecdoteTag(it.id!!, it.name!!) }