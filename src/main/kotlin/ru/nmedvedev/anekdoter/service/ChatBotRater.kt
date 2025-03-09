package ru.nmedvedev.anekdoter.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.client.ChatBotClient
import ru.nmedvedev.anekdoter.config.ApplicationProperties
import ru.nmedvedev.anekdoter.model.Anecdote
import ru.nmedvedev.anekdoter.service.event.AnecdoteCreatedEvent
import java.util.concurrent.CompletableFuture

private val logger = KotlinLogging.logger {}



@Component
@ConditionalOnProperty(
    value = ["anekdoter.background.chatbots-rater.enabled"],
    havingValue = "true",
    matchIfMissing = false
)
class ChatBotRater(
    private val client: ChatBotClient,
    applicationProperties: ApplicationProperties,
    private val anecdoteService: AnecdoteService,
) {

    private val models = applicationProperties.chatbots.values.flatMap { it.models }

    @EventListener(AnecdoteCreatedEvent::class)
    protected fun onAnecdoteCreated(event: AnecdoteCreatedEvent) {
        models.forEach { model ->
            getRateAnecdote(model, event.anecdote)
                .thenAccept { rate ->
                    if (rate != null) {
                        anecdoteService.rate(model, event.anecdote.id!!, rate)
                    }
                }
        }
    }

    private fun getRateAnecdote(model: String, anecdote: Anecdote): CompletableFuture<Int?> {
        val prompt = BASE_PROMPT.format(anecdote.text!!, anecdote.tags!!.joinToString(",") { it.name!! })
        logger.debug { "prompt: $prompt" }
        return client.requestWithModel(prompt, model)
            .exceptionally { ex ->
                logger.error(ex) { "Failed to obtain rate for $model" }
                null
            }.thenApply { result ->
                val parsed = (result?.toIntOrNull() ?: result?.fetchFirstDigit())?.takeIf { it in 0..5 }

                if (parsed == null) {
                    logger.warn { "Failed to parse rate: $result from $model" }
                } else {
                    logger.info { "$model rated as $parsed" }
                }
                parsed
            }
    }

    fun rateAnecdote(anecdote: Anecdote): ModelAndRate? {
        val model = models.random()
        val rate = getRateAnecdote(model, anecdote).get()
        return rate?.let { ModelAndRate(model, it) }
    }
}

private fun String.fetchFirstDigit(): Int? {
    return firstOrNull { it.isDigit() }?.digitToInt()
}

data class ModelAndRate(
    val model: String,
    val rate: Int,
)

private const val BASE_PROMPT =
    "Вот тебе шутка:\n```\n%s\n```\nОцени эту шутку от 0 до 5, в критериях учти то, как хорошо он подходит под понятие анекдот, как в классическом русском стиле, так же оцени, как раскрываются следующие темы в анекдоте: %s. В ответ верни ТОЛЬКО оценку одной цифрой"