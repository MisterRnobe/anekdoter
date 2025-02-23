package ru.nmedvedev.anekdoter.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.VirtualThreadTaskExecutor
import org.springframework.web.client.RestTemplate
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor

@Configuration
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration {

    @Bean
    fun defaultRestTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()

    @Bean
    fun logbookInterceptor(logbook: Logbook): RestTemplateCustomizer = RestTemplateCustomizer {
        it.interceptors.add(LogbookClientHttpRequestInterceptor(logbook))
    }

    @Bean
    fun asyncTaskExecutor(): AsyncTaskExecutor = VirtualThreadTaskExecutor("async-")

}