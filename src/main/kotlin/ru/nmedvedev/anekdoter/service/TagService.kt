package ru.nmedvedev.anekdoter.service

import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.repository.TagRepository
import java.util.UUID

@Component
class TagService(
    private val tagRepository: TagRepository
) {

    fun getAll(): List<TopicDto> {
        return tagRepository.findAll().map { TopicDto(it.id!!, it.name!!) }
    }
}

data class TopicDto(val id: UUID, val title: String)