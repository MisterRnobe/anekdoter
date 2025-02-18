package ru.nmedvedev.anekdoter.service

import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.client.ChatGPTClient
import ru.nmedvedev.anekdoter.client.DeepSeekClient

@Component
class AnecdoteGenerator(
    private val deepSeekClient: DeepSeekClient,
    private val chatGPTClient: ChatGPTClient,
) {
    fun generate(): String {
        return chatGPTClient.request(BASE_PROMPT) ?: TODO()
    }
}

private const val BASE_PROMPT = "Придумай мне анекдот, в ответ верни только его текст"