package ru.nmedvedev.anekdoter.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.client.ChatBotClient
import ru.nmedvedev.anekdoter.config.ApplicationProperties
import ru.nmedvedev.anekdoter.model.Anecdote
import ru.nmedvedev.anekdoter.service.event.AnecdoteCreatedEvent

private val logger = KotlinLogging.logger {}

@Component
class ChatBotRater(
    private val client: ChatBotClient,
    applicationProperties: ApplicationProperties,
    private val anecdoteService: AnecdoteService,
    private val asyncTaskExecutor: AsyncTaskExecutor,
) {

    private val models = applicationProperties.chatbots.values.flatMap { it.models }

    @EventListener(AnecdoteCreatedEvent::class)
    protected fun onAnecdoteCreated(event: AnecdoteCreatedEvent) =
        models.forEach { model ->
            asyncTaskExecutor.submit {
                try {
                    val rate = rateAnecdote(model, event.anecdote)
                    if (rate != null) {
                        anecdoteService.rate(model, event.anecdote.id!!, rate)
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to rate anecdote" }
                }
            }
        }

    private fun rateAnecdote(model: String, anecdote: Anecdote): Int? {
        val prompt = BASE_PROMPT.format(anecdote.text!!)
        logger.debug { "prompt: $prompt" }
        val result = client.requestWithModel(prompt, model).join()
        val parsed = result?.toIntOrNull()?.takeIf { it in 0..5 }

        if (parsed == null) {
            logger.warn { "Failed to parse rate: $result from $model" }
        } else {
            logger.info { "$model rated as $parsed" }
        }
        return parsed
    }

    fun rateAnecdote(anecdote: Anecdote): ModelAndRate? {
        val model = models.random()
        val rate = rateAnecdote(model, anecdote)
        return rate?.let { ModelAndRate(model, it) }
    }
}

data class ModelAndRate(
    val model: String,
    val rate: Int,
)

private const val BASE_PROMPT =
    "Вот тебе анекдот:\n```\n%s\n```\nОцени юмор и сюжет в этом анекдоте по шкале от 1 до 5, в ответ верни только оценку одной цифрой"