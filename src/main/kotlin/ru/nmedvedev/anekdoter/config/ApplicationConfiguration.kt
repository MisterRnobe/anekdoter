package ru.nmedvedev.anekdoter.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

@Configuration
class ApplicationConfiguration {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder.build()
    }

    @Bean
    fun logbookInterceptor(logbook: Logbook): RestTemplateCustomizer = RestTemplateCustomizer {
        it.interceptors.add(LogbookClientHttpRequestInterceptor(logbook))
    }
}