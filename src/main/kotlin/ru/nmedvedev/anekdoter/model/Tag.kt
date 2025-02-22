package ru.nmedvedev.anekdoter.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity(name = "Tag")
@Table(name = "tag")
data class Tag(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(nullable = false)
    var name: String? = null,

    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "tag_anecdote",
        joinColumns = [JoinColumn(name = "tag_id")],
        inverseJoinColumns = [JoinColumn(name = "anecdote_id")],
    )
    var anecdotes: List<Anecdote>? = null,

    @CreationTimestamp
    var createdAt: Instant? = null,
)