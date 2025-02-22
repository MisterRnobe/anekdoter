package ru.nmedvedev.anekdoter.service

import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.client.ChatGPTClient
import ru.nmedvedev.anekdoter.client.ChatGPTModel
import ru.nmedvedev.anekdoter.client.DeepSeekClient

@Component
class AnecdoteGenerator(
    private val deepSeekClient: DeepSeekClient,
    private val chatGPTClient: ChatGPTClient,
) {

    fun generateFor(sessionId: String, tags: List<String>): AnecdoteMeta {
        val model = ChatGPTModel.GPT_4O

        val request = chatGPTClient.request(
            BASE_PROMPT.format(
                tags.joinToString("\n") { tag -> "* $tag" },
            )
        ) ?: TODO()

        return AnecdoteMeta(request, model.name)
    }

}

data class AnecdoteMeta(
    val text: String,
    val model: String,
)

private const val BASE_PROMPT = "Придумай мне такой смешной анекдот с панчлайном, который бы включал в себя следующие темы:\n%s\n. Сделай сюжет в нем таким, чтобы он не был вычурным и рваным, чтобы все темы выглядили гармонично, для этого можешь сгладить чутка упомянутые мной темы. В ответ верни только его текст"
// private const val BASE_PROMPT = "Придумай мне такую шутку, которая бы включала в себя следующие темы:\n%s\n. Сделай шутку такой, чтобы она не был вычурной и рваной, чтобы все темы гармонично ложились в штуку, для этого можешь сгладить чутка упомянутые мной темы. В ответ верни только его текст"