package ru.nmedvedev.anekdoter.repository

import org.junit.jupiter.api.Test
import ru.nmedvedev.anekdoter.BaseTest
import ru.nmedvedev.anekdoter.model.Anecdote
import ru.nmedvedev.anekdoter.model.Tag
import ru.nmedvedev.anekdoter.randomString
import kotlin.test.assertEquals

class AnecdoteRepositoryTest : BaseTest() {

    @Test
    fun `should return top rated`() {
        val a1 = anecdoteRepository.save(Anecdote(text = randomString(), createdBy = randomString()))
        val a2 = anecdoteRepository.save(Anecdote(text = randomString(), createdBy = randomString()))
        val a3 = anecdoteRepository.save(Anecdote(text = randomString(), createdBy = randomString()))
        val a4 = anecdoteRepository.save(Anecdote(text = randomString(), createdBy = randomString()))

        // (3 + 4 + 5 + 3) / 4 = 3.75
        anecdoteService.rate(randomString(), a1.id!!, 3)
        anecdoteService.rate(randomString(), a1.id!!, 4)
        anecdoteService.rate(randomString(), a1.id!!, 5)
        anecdoteService.rate(randomString(), a1.id!!, 3)
        // (3 + 2) / 2 = 2.5
        anecdoteService.rate(randomString(), a2.id!!, 3)
        anecdoteService.rate(randomString(), a2.id!!, 2)
        // (4 + 5 + 4) / 3 = 4.33
        anecdoteService.rate(randomString(), a3.id!!, 4)
        anecdoteService.rate(randomString(), a3.id!!, 5)
        anecdoteService.rate(randomString(), a3.id!!, 4)
        // 2
        anecdoteService.rate(randomString(), a4.id!!, 2)

        val result = anecdoteRepository.findTopRatedFrom(a1.createdAt!!.minusSeconds(99999))

        assertEquals(a3.id, result!!.id!!)
    }
}