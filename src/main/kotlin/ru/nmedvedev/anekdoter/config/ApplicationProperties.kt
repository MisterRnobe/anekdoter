package ru.nmedvedev.anekdoter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI

@ConfigurationProperties(prefix = "anekdoter")
data class ApplicationProperties(
    val client: Clients,
    val tags: TagsProperties,
)

data class Clients(
    val deepseek: ClientProperties,
    val chatgpt: ClientProperties,
)

data class ClientProperties(
    val baseUrl: URI,
    val authorization: String,
)
data class TagsProperties(val initial: List<String>)