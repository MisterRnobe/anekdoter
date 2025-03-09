package ru.nmedvedev.anekdoter.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import ru.nmedvedev.anekdoter.config.ApplicationProperties
import java.util.concurrent.CompletableFuture

@Component
class TelegramClient(
    private val restTemplate: RestTemplate,
    applicationProperties: ApplicationProperties,
) {

    private val tgProperties = applicationProperties.telegram

    @Async
    fun postMessage(text: String): CompletableFuture<ResponseEntity<*>> =
        CompletableFuture.completedFuture(postMessageInternal(text))

    private fun postMessageInternal(text: String): ResponseEntity<*> {
        return restTemplate.postForEntity<Void>(
            url = "${tgProperties.baseUrl}{bot_token}/sendMessage",
            request = mapOf("chat_id" to tgProperties.chatId, "text" to text),
            uriVariables = mapOf("bot_token" to tgProperties.botToken)
        )
    }
}