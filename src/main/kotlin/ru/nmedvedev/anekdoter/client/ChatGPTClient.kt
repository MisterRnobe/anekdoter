package ru.nmedvedev.anekdoter.client

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity

@Component
class ChatGPTClient(
    @Qualifier("chatGPTRestTemplate")
    private val restTemplate: RestTemplate,
) {

    fun request(prompt: String, model: ChatGPTModel = ChatGPTModel.GPT_4O): String? {
        return restTemplate.postForEntity<ChatGPTResponse>(
            url = "/v1/chat/completions",
            request = ChatGPTRequest(model, listOf(ChatGPTRequestMessage("user", prompt)), false),
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