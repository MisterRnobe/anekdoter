package ru.nmedvedev.anekdoter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "anekdoter")
data class ApplicationProperties(
    val chatbots: Map<String, ChatBotProperties>,
    val tags: TagsProperties,
    val telegram: TelegramProperties,
)

data class ChatBotProperties(
    val url: URI,
    val headers: Map<String, String>,
    val models: List<String>,
    val defaultModel: String,
    val requestMask: String,
    val responseTextJsonPath: String,
)

data class TagsProperties(val initial: List<String>)

data class TelegramProperties(
    val baseUrl: URI,
    val botToken: String,
    val chatId: String,
)