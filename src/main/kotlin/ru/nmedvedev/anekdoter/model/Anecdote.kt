package ru.nmedvedev.anekdoter.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity(name = "Anecdote")
@Table(name = "anecdote")
data class Anecdote(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(nullable = false)
    var text: String? = null,

    @Column(nullable = false)
    var createdBy: String? = null,

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinTable(
        name = "tag_anecdote",
        joinColumns = [JoinColumn(name = "anecdote_id") ],
        inverseJoinColumns = [JoinColumn(name = "tag_id") ]
    )
    var tags: List<Tag>? = null,

    @CreationTimestamp
    var createdAt: Instant? = null,
)