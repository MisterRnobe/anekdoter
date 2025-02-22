package ru.nmedvedev.anekdoter.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.validation.annotation.Validated
import ru.nmedvedev.anekdoter.model.Tag
import java.util.UUID

interface TagRepository : JpaRepository<Tag, UUID> {

    @Validated
    @Query("SELECT * FROM tag ORDER BY random() LIMIT ?", nativeQuery = true)
    fun findAny(count: Int): List<Tag>

}