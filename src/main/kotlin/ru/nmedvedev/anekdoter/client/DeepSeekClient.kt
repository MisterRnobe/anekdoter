package ru.nmedvedev.anekdoter.client

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Component
class DeepSeekClient(
    @Qualifier("deepSeekRestTemplate")
    private val restTemplate: RestTemplate,
) {

    fun request(prompt: String): String? {
        restTemplate.postForEntity<Map<String, Any>>(
            url = "/chat/completions",
            request = DeepSeekRequest("deepseek-chat", listOf(DeepSeekRequestMessage("user", prompt)), false),
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