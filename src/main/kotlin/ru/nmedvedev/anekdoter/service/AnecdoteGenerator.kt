package ru.nmedvedev.anekdoter.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.client.ChatBotClient
import ru.nmedvedev.anekdoter.config.ApplicationProperties
import java.util.concurrent.TimeUnit

private val logger = KotlinLogging.logger {}

@Component
class AnecdoteGenerator(
    private val chatBotClient: ChatBotClient,
    applicationProperties: ApplicationProperties,
) {

    private val models = applicationProperties.chatbots.values.flatMap { it.models }

    fun generateFor(sessionId: String, tags: List<String>): AnecdoteMeta {
        val model = models.random()
        logger.info { "Requesting with $model" }

        val request = chatBotClient.requestWithModel(
            BASE_PROMPT.format(tags.joinToString("\n") { tag -> "* $tag" }),
            model
        ).orTimeout(1, TimeUnit.MINUTES).join() ?: TODO()

        return AnecdoteMeta(request, model)
    }
}

data class AnecdoteMeta(
    val text: String,
    val model: String,
)

private const val BASE_PROMPT =
    "Придумай мне анекдот с панчлайном, как в классическом русском стиле, который бы включал в себя следующие темы:\n%s\nСделай сюжет в нем таким, чтобы он не был вычурным и рваным, чтобы все темы выглядили гармонично, для этого можешь сгладить чутка упомянутые мной темы. В ответ верни только его текст"
// private const val BASE_PROMPT = "Придумай мне такую шутку, которая бы включала в себя следующие темы:\n%s\n. Сделай шутку такой, чтобы она не был вычурной и рваной, чтобы все темы гармонично ложились в штуку, для этого можешь сгладить чутка упомянутые мной темы. В ответ верни только его текст"