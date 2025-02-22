package ru.nmedvedev.anekdoter.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration {

    @Bean
    fun deepSeekRestTemplate(builder: RestTemplateBuilder, applicationProperties: ApplicationProperties): RestTemplate {
        return builder
            .defaultHeader("Authorization", applicationProperties.client.deepseek.authorization)
            .rootUri(applicationProperties.client.deepseek.baseUrl.toString())
            .build()
    }

    @Bean
    fun chatGPTRestTemplate(builder: RestTemplateBuilder, applicationProperties: ApplicationProperties): RestTemplate {
        return builder
            .defaultHeader("Authorization", applicationProperties.client.chatgpt.authorization)
            .rootUri(applicationProperties.client.chatgpt.baseUrl.toString())
            .build()
    }

    @Bean
    fun logbookInterceptor(logbook: Logbook): RestTemplateCustomizer = RestTemplateCustomizer {
        it.interceptors.add(LogbookClientHttpRequestInterceptor(logbook))
    }
}