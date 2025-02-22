package ru.nmedvedev.anekdoter.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import ru.nmedvedev.anekdoter.service.TagService
import java.util.UUID

@ResponseBody
@RestController
@RequestMapping("/api/v1")
class TopicController(
    private val tagService: TagService
) {

    @GetMapping("/tags", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAllTopics(): List<TagResponse> {
        return tagService.getAll().map { TagResponse(it.id, it.title) }
    }
}

data class TagResponse(
    val id: UUID,
    val name: String,
)