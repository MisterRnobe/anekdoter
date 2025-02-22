package ru.nmedvedev.anekdoter.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.nmedvedev.anekdoter.model.Anecdote
import ru.nmedvedev.anekdoter.model.AnecdoteWithRating
import java.util.UUID

interface AnecdoteRepository: JpaRepository<Anecdote, UUID> {

    @Query("""
        SELECT a.*, coalesce(r.rating_sum, 0) as rating_sum, coalesce(r.rating_count, 0) as rating_count, t.* FROM anecdote AS a
        left join (select t.anecdote_id, count(t.anecdote_id) as rating_count, sum(t.rate) as rating_sum from rate as t group by t.anecdote_id) as r on a.id = r.anecdote_id
        left join tag_anecdote ta on a.id = ta.anecdote_id
        left join tag t on ta.tag_id = t.id
        where a.id not in (select a1.id from anecdote as a1 left join rate as r1 on a1.id = r1.anecdote_id where r1.session_id = ?)
        order by random()
        limit 1
    """, nativeQuery = true)
    fun findOneUnratedWithRating(sessionId: String): AnecdoteWithRating?

    @Query("""
        SELECT a.*, t.*, coalesce(r.rating_sum, 0) as rating_sum, coalesce(r.rating_count, 0) as rating_count FROM anecdote AS a
        left join (select t.anecdote_id, count(t.anecdote_id) as rating_count, sum(t.rate) as rating_sum from rate as t group by t.anecdote_id) as r on a.id = r.anecdote_id
        left join tag_anecdote ta on a.id = ta.anecdote_id
        left join tag t on ta.tag_id = t.id
        where a.id not in (select a1.id from anecdote as a1 left join rate as r1 on a1.id = r1.anecdote_id where r1.session_id = ?)
        order by random()
        limit 1
    """, nativeQuery = true)
    fun findOneUnratedWithRating(sessionId: String, tags: List<UUID>): AnecdoteWithRating?

    @Query("""
        SELECT a FROM Anecdote a WHERE a.id NOT IN (SELECT r1.anecdote.id FROM Rate as r1 where r1.sessionId = :sessionId) ORDER BY RANDOM() LIMIT 1
    """)
    fun findOneUnrated(sessionId: String): Anecdote?

    @Query("""
        SELECT a FROM Anecdote a JOIN a.tags t WHERE a.id NOT IN (SELECT r1.anecdote.id FROM Rate as r1 where r1.sessionId = :sessionId)
        AND t.id IN :tags ORDER BY RANDOM() LIMIT 1
    """)
    fun findOneUnrated(sessionId: String, tags: List<UUID>): Anecdote?

    @Query("""
        SELECT r.anecdote.id, sum(r.rate) as ratingSum, count(r.anecdote.id) as ratingCount FROM Rate r GROUP BY r.anecdote.id HAVING r.anecdote.id = :id
    """)
    fun rateAnecdote(id: UUID): AnecdoteWithRating?

}