package ru.nmedvedev.anekdoter.client

import com.fasterxml.jackson.databind.JsonNode
import com.google.common.util.concurrent.RateLimiter
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.text.StringSubstitutor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import ru.nmedvedev.anekdoter.config.ApplicationProperties
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Component
class ChatBotClient(
    @Qualifier("defaultRestTemplate")
    private val restTemplate: RestTemplate,
    private val asyncTaskExecutor: AsyncTaskExecutor,
    applicationProperties: ApplicationProperties,
) {

    private val chatBots = applicationProperties.chatbots
    private val modelToChatbot = applicationProperties.chatbots
        .entries
        .flatMap { (k, v) -> v.models.map { it to k } }
        .associate { it }
    private val rateLimiter = RateLimiter.create(2.0)

    fun requestWithModel(prompt: String, model: String): CompletableFuture<String?> {
        val bot = chatBots[modelToChatbot[model]] ?: throw IllegalArgumentException("ChatBot not found for model $model")

        val request = StringSubstitutor.replace(bot.requestMask, mapOf("model" to model, "prompt" to prompt.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "")))

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        bot.headers.forEach { (key, value) -> headers.add(key, value) }

        return CompletableFuture.supplyAsync({
            rateLimiter.acquire()
            logger.info { "requesting: ${bot.url}, request: $request" }
            val resp = restTemplate.exchange<JsonNode>(
                url = bot.url,
                method = HttpMethod.POST,
                requestEntity = HttpEntity(request, headers),
            )
            resp.body?.at(bot.responseTextJsonPath)?.asText()?.takeIf { it.isNotBlank() }
        }, asyncTaskExecutor)
            // .orTimeout(30, TimeUnit.SECONDS)
            // .exceptionally { e ->
            //     logger.error(e) { "Failed to request $model" }
            //     throw e
            // }
    }

    fun requestWithChatBot(prompt: String, chatbot: String): CompletableFuture<String?> =
        requestWithModel(prompt, chatBots[chatbot]?.defaultModel ?: throw IllegalArgumentException("ChatBot not found: $chatbot"))
}