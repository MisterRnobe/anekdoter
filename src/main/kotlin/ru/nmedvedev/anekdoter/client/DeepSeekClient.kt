package ru.nmedvedev.anekdoter.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.http.MediaType
import java.net.URL

@Component
class DeepSeekClient(
    private val restTemplate: RestTemplate,
    @Value("\${anekdoter.client.deepseek.base-url}")
    private val baseUrl: URL,
    @Value("\${anekdoter.client.deepseek.authorization}")
    private val authorization: String,
) {

    fun request(prompt: String): String? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers["Authorization"] = authorization

        restTemplate.exchange<Map<String, Any>>(
            url = "$baseUrl/chat/completions",
            method = HttpMethod.POST,
            requestEntity = HttpEntity<DeepSeekRequest>(
                DeepSeekRequest("deepseek-chat", listOf(DeepSeekRequestMessage("user", prompt)), false),
                headers
            ),
        )
        return null
    }
}

private data class DeepSeekRequest(
    val model: String,
    val messages: List<DeepSeekRequestMessage>,
    val stream: Boolean,
)

private data class DeepSeekRequestMessage(
    val role: String,
    val content: String,
)