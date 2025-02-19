package ru.nmedvedev.anekdoter.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mockito
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import ru.nmedvedev.anekdoter.BaseTest
import java.math.BigDecimal
import java.util.UUID

class AnecdoteServiceTest : BaseTest() {

    @Test
    fun `should generate new anecdote on empty anecdotes`() {
        val sessionId = UUID.randomUUID().toString()
        val text = UUID.randomUUID().toString()
        Mockito.doReturn(text).`when`(anecdoteGenerator).generate()

        val result = anecdoteService.suggestAnecdote(sessionId)

        assertEquals(text, result.text)
    }

    @Test
    fun `should generate new anecdote if all anecdotes are rated`() {
        val sessionId = UUID.randomUUID().toString()
        val text = UUID.randomUUID().toString()
        Mockito.doReturn(UUID.randomUUID().toString()).`when`(anecdoteGenerator).generate()
        (1..10).forEach { _ ->
            val suggestAnecdote = anecdoteService.suggestAnecdote(sessionId)
            anecdoteService.rate(sessionId, suggestAnecdote.id, 5)
        }
        clearInvocations(anecdoteGenerator)
        Mockito.doReturn(text).`when`(anecdoteGenerator).generate()

        val result = anecdoteService.suggestAnecdote(sessionId)

        assertEquals(text, result.text)
        verify(anecdoteGenerator).generate()
    }

    @Test
    fun `should return existing anecdote if has unrated anecdotes`() {
        val session1 = UUID.randomUUID().toString()
        val session2 = UUID.randomUUID().toString()
        Mockito.doReturn(UUID.randomUUID().toString()).`when`(anecdoteGenerator).generate()
        (1..10).forEach { _ ->
            val suggestAnecdote = anecdoteService.suggestAnecdote(session2)
            anecdoteService.rate(session1, suggestAnecdote.id, 5)
            anecdoteService.rate(session2, suggestAnecdote.id, 5)
        }
        val last = anecdoteService.suggestAnecdote(session1)
        clearInvocations(anecdoteGenerator)

        val result = anecdoteService.suggestAnecdote(session2)

        assertEquals(last.text, result.text)
        verifyNoInteractions(anecdoteGenerator)
    }

    @Test
    fun `should correctly calculate rating and count`() {
        Mockito.doReturn(UUID.randomUUID().toString()).`when`(anecdoteGenerator).generate()
        val anecdote = anecdoteService.suggestAnecdote(UUID.randomUUID().toString())
        (1..10).forEach { i ->
            anecdoteService.rate(UUID.randomUUID().toString(), anecdote.id, i)
        }

        val result = anecdoteService.suggestAnecdote(UUID.randomUUID().toString())

        assertAll(
            { assertEquals(anecdote.id, result.id) },
            // (1+2+3..+10) / 10 = 55/10 = 5.5
            { assertEquals(BigDecimal("5.5"), result.rating) },
            { assertEquals(10, result.ratingCount) },
        )
    }
}