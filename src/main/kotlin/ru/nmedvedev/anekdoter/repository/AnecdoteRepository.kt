package ru.nmedvedev.anekdoter.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.nmedvedev.anekdoter.model.Anecdote
import java.util.UUID

interface AnecdoteRepository: JpaRepository<Anecdote, UUID> {

    @Query("SELECT a.* FROM anecdote AS a where a.id not in (select a1.id from anecdote as a1 left join rate as r1 on a1.id = r1.anecdote_id where r1.session_id = ?) LIMIT 1", nativeQuery = true)
    fun findOneUnrated(sessionId: String): Anecdote?

}