package ru.nmedvedev.anekdoter.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.nmedvedev.anekdoter.model.Anecdote
import ru.nmedvedev.anekdoter.model.AnecdoteWithRating
import java.util.UUID

interface AnecdoteRepository: JpaRepository<Anecdote, UUID> {

    @Query("""
        SELECT a.*, r.rating_sum as rating_sum, r.rating_count as rating_count FROM anecdote AS a 
        left join (select t.anecdote_id, count(t.anecdote_id) as rating_count, sum(t.rate) as rating_sum from rate as t group by t.anecdote_id) as r on a.id = r.anecdote_id
        where a.id not in (select a1.id from anecdote as a1 left join rate as r1 on a1.id = r1.anecdote_id where r1.session_id = ?)
        limit 1
    """, nativeQuery = true)
    fun findOneUnratedWithRating(sessionId: String): AnecdoteWithRating?

}