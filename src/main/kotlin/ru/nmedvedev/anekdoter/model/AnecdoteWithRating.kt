package ru.nmedvedev.anekdoter.model

import java.util.UUID

interface AnecdoteWithRating {

    fun getId(): UUID

    fun getText(): String

    fun getRatingSum(): Long

    fun getRatingCount(): Int

    fun getTags(): List<Tag>

}