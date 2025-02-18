package ru.nmedvedev.anekdoter.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "anecdote")
data class Anecdote(
    @Id
    var id: UUID? = null,

    @Column(nullable = false)
    var text: String? = null,

    @CreationTimestamp
    var createdAt: Instant? = null,
)