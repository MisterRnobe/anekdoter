package ru.nmedvedev.anekdoter.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity(name = "Rate")
@Table(name = "rate")
data class Rate(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(nullable = false)
    var sessionId: String? = null,

    @Column(nullable = false)
    var rate: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "anecdote_id")
    var anecdote: Anecdote? = null,
)