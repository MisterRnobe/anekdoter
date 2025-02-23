package ru.nmedvedev.anekdoter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "anekdoter")
data class ApplicationProperties(
    val chatbots: Map<String, ChatBotProperties>,
    val tags: TagsProperties,
)

data class ChatBotProperties(
    val url: URI,
    val authorization: String,
    val models: List<String>,
    val defaultModel: String,
    val requestMask: String,
    val responseTextJsonPath: String,
)

data class TagsProperties(val initial: List<String>)