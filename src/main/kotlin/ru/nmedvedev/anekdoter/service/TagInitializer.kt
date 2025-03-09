package ru.nmedvedev.anekdoter.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import ru.nmedvedev.anekdoter.config.ApplicationProperties
import ru.nmedvedev.anekdoter.model.Tag
import ru.nmedvedev.anekdoter.repository.TagRepository

private val logger = KotlinLogging.logger {}

@Component
class TagInitializer(
    private val applicationProperties: ApplicationProperties,
    private val tagRepository: TagRepository,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val existing = tagRepository.findAll().map { it.name!! }.toSet()
        val tagNames = applicationProperties.tags.initial.distinct().filter { !existing.contains(it) }

        logger.info { "tags to save $tagNames" }
        tagRepository.saveAll(tagNames.map { Tag(name = it) })
        logger.info { "Saved tags $tagNames" }
    }
}
