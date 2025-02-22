package ru.nmedvedev.anekdoter.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.any
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import ru.nmedvedev.anekdoter.BaseTest
import ru.nmedvedev.anekdoter.anecdoteMeta
import ru.nmedvedev.anekdoter.model.Tag
import ru.nmedvedev.anekdoter.randomString
import java.math.BigDecimal
import kotlin.test.assertNotEquals

class AnecdoteServiceTest : BaseTest() {

    @Test
    fun `should generate new anecdote on empty anecdotes`() {
        val sessionId = randomString()
        val anecdoteMeta = anecdoteMeta()
        doReturn(anecdoteMeta).whenever(anecdoteGenerator).generateFor(eq(sessionId), any())

        val result = anecdoteService.suggestAnecdote(sessionId, null)

        assertEquals(anecdoteMeta.text, result.text)
    }

    @Test
    fun `should generate new anecdote if all anecdotes are rated`() {
        val sessionId = randomString()
        val anecdoteMeta = anecdoteMeta()
        doReturn(anecdoteMeta()).whenever(anecdoteGenerator).generateFor(eq(sessionId), any())
        (1..10).forEach { _ ->
            val suggestAnecdote = anecdoteService.suggestAnecdote(sessionId, null)
            anecdoteService.rate(sessionId, suggestAnecdote.id, 5)
        }
        clearInvocations(anecdoteGenerator)
        doReturn(anecdoteMeta).whenever(anecdoteGenerator).generateFor(eq(sessionId), any())

        val result = anecdoteService.suggestAnecdote(sessionId, null)

        assertEquals(anecdoteMeta.text, result.text)
        verify(anecdoteGenerator).generateFor(eq(sessionId), any())
    }

    @Test
    fun `should return existing anecdote if has unrated anecdotes`() {
        val session1 = randomString()
        val session2 = randomString()
        doReturn(anecdoteMeta()).whenever(anecdoteGenerator).generateFor(any(), any())
        (1..10).forEach { _ ->
            val suggestAnecdote = anecdoteService.suggestAnecdote(session2, null)
            anecdoteService.rate(session1, suggestAnecdote.id, 5)
            anecdoteService.rate(session2, suggestAnecdote.id, 5)
        }
        val last = anecdoteService.suggestAnecdote(session1,null)
        clearInvocations(anecdoteGenerator)

        val result = anecdoteService.suggestAnecdote(session2, null)

        assertAll(
            { assertEquals(last.text, result.text) },
            { assertEquals(tagRepository.findAll().map { it.id }.sortedBy { it }, result.tags.map { it.id }.sortedBy { it }) },
        )
        verifyNoInteractions(anecdoteGenerator)
    }

    @Test
    fun `should correctly calculate rating and count`() {
        doReturn(anecdoteMeta()).whenever(anecdoteGenerator).generateFor(any(), any())
        val anecdote = anecdoteService.suggestAnecdote(randomString(), null)
        (1..10).forEach { i ->
            anecdoteService.rate(randomString(), anecdote.id, i)
        }

        val result = anecdoteService.suggestAnecdote(randomString(), null)

        assertAll(
            { assertEquals(anecdote.id, result.id) },
            // (1+2+3..+10) / 10 = 55/10 = 5.5
            { assertEquals(BigDecimal("5.5"), result.rating) },
            { assertEquals(10, result.ratingCount) },
        )
    }

    @Test
    fun `should generate new anecdote if has no anecdote for given tags`() {
        tagRepository.deleteAll()
        val tag1 = tagRepository.save(Tag(name = randomString()))
        val tag2 = tagRepository.save(Tag(name = randomString()))
        val tag3 = tagRepository.save(Tag(name = randomString()))
        doReturn(anecdoteMeta()).whenever(anecdoteGenerator).generateFor(any(), any())
        val anecdote = anecdoteService.suggestAnecdote(randomString(), listOf(tag1.id!!, tag2.id!!))
        clearInvocations(anecdoteGenerator)

        val sessionId = randomString()
        val result = anecdoteService.suggestAnecdote(sessionId, listOf(tag3.id!!))

        assertAll(
            { assertNotEquals(anecdote.id, result.id) },
            { assertEquals(listOf(tag3.id!!), result.tags.map { it.id }) },
        )
        verify(anecdoteGenerator).generateFor(sessionId, listOf(tag3.name!!))
    }

    @Test
    fun `should not generate new anecdote if has anecdote for given tags`() {
        tagRepository.deleteAll()
        val tag1 = tagRepository.save(Tag(name = randomString()))
        val tag2 = tagRepository.save(Tag(name = randomString()))
        val tag3 = tagRepository.save(Tag(name = randomString()))
        doReturn(anecdoteMeta()).whenever(anecdoteGenerator).generateFor(any(), any())
        val anecdote = anecdoteService.suggestAnecdote(randomString(), listOf(tag1.id!!, tag2.id!!, tag3.id!!))
        clearInvocations(anecdoteGenerator)

        val sessionId = randomString()
        val result = anecdoteService.suggestAnecdote(sessionId, listOf(tag3.id!!))

        assertAll(
            { assertEquals(anecdote.id, result.id) },
            { assertEquals(setOf(tag1.id!!, tag2.id!!, tag3.id!!), result.tags.map { it.id }.toSet()) },
        )
        verifyNoInteractions(anecdoteGenerator)
    }

}