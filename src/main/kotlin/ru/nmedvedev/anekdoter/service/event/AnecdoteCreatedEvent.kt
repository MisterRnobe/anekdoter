package ru.nmedvedev.anekdoter.service.event

import ru.nmedvedev.anekdoter.model.Anecdote

data class AnecdoteCreatedEvent(
    val anecdote: Anecdote
)