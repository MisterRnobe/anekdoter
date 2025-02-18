package ru.nmedvedev.anekdoter.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.nmedvedev.anekdoter.service.AnecdoteService
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class AnecdoteController(
    private val anecdoteService: AnecdoteService,
) {

    @GetMapping("/anecdote")
    fun getAnecdote(): AnecdoteDto {
        return anecdoteService.suggestAnecdote(UUID.randomUUID().toString()).let {
            AnecdoteDto(it.id, it.text)
        }
    }

    @PostMapping("/anecdote/{id}/rate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun rateAnecdote(@PathVariable id: UUID) {
        anecdoteService.rate(UUID.randomUUID().toString(), id, 5)
    }
}

data class AnecdoteDto(
    private val id: UUID,
    private val anecdote: String,
)