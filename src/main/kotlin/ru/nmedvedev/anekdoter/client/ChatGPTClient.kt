package ru.nmedvedev.anekdoter.client

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.net.URL

@Component
class ChatGPTClient(
    private val restTemplate: RestTemplate,
    @Value("\${anekdoter.client.chatgpt.base-url}")
    private val baseUrl: URL,
    @Value("\${anekdoter.client.chatgpt.authorization}")
    private val authorization: String,
) {

    fun request(prompt: String, model: ChatGPTModel = ChatGPTModel.GPT_4O): String? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers["Authorization"] = authorization

        return restTemplate.exchange<ChatGPTResponse>(
            url = "$baseUrl/v1/chat/completions",
            method = HttpMethod.POST,
            requestEntity = HttpEntity<ChatGPTRequest>(
                ChatGPTRequest(model, listOf(ChatGPTRequestMessage("user", prompt)), false),
                headers
            ),
        ).body?.choices?.firstOrNull()?.message?.content
    }
}

enum class ChatGPTModel {
    @JsonProperty("gpt-4o")
    GPT_4O,
}

private data class ChatGPTRequest(
    val model: ChatGPTModel,
    val messages: List<ChatGPTRequestMessage>,
    val stream: Boolean,
)

private data class ChatGPTRequestMessage(
    val role: String,
    val content: String,
)

private data class ChatGPTResponse(
    val choices: List<ChatGPTResponseChoice>,
)

private data class ChatGPTResponseChoice(
    val message: ChatGPTResponseChoiceMessage,
)

private data class ChatGPTResponseChoiceMessage(
    val role: String,
    val content: String,
    val refusal: Any?,
)