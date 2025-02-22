package ru.nmedvedev.anekdoter.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.nmedvedev.anekdoter.service.AnecdoteService
import java.math.BigDecimal
import java.util.UUID

@Validated
@ResponseBody
@RestController
@RequestMapping("/api/v1")
class AnecdoteController(
    private val anecdoteService: AnecdoteService,
) {

    @GetMapping("/anecdote", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAnecdote(@RequestHeader(value = SESSION_ID_HEADER, required = true) sessionId: String, @RequestParam(value = "tag_id", required = false) tagIds: List<UUID>?): AnecdoteDto {
        return anecdoteService.suggestAnecdote(sessionId, tagIds).let {
            AnecdoteDto(it.id, it.text, it.rating, it.ratingCount, it.tags.map { TagResponse(it.id, it.name) })
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/anecdote/{id}/rate", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun rateAnecdote(@PathVariable id: UUID, @RequestHeader(value = SESSION_ID_HEADER, required = true) sessionId: String, @RequestBody body: RateAnecdoteRequest) {
        anecdoteService.rate(sessionId, id, body.rate)
    }
}

const val SESSION_ID_HEADER = "X-Session-Id"

data class AnecdoteDto(
    val id: UUID,
    val anecdote: String,
    val rating: BigDecimal,
    val ratingCount: Int,
    val tags: List<TagResponse>,
)

data class RateAnecdoteRequest(
    val rate: Int,
)