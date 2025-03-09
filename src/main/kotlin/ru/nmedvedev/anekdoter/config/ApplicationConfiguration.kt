package ru.nmedvedev.anekdoter.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.VirtualThreadTaskExecutor
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler
import org.springframework.web.client.RestTemplate
import org.zalando.logbook.Logbook
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor
import java.time.Clock

@EnableAsync
@Configuration
@EnableScheduling
@EnableConfigurationProperties(ApplicationProperties::class)
class ApplicationConfiguration {

    @Bean
    fun defaultRestTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()

    @Bean
    fun logbookInterceptor(logbook: Logbook): RestTemplateCustomizer = RestTemplateCustomizer {
        it.interceptors.add(LogbookClientHttpRequestInterceptor(logbook))
    }

    @Bean(name = ["taskExecutor", "asyncTaskExecutor"])
    fun asyncTaskExecutor(): AsyncTaskExecutor = VirtualThreadTaskExecutor("async-")

    @Bean
    fun taskScheduler(): TaskScheduler = SimpleAsyncTaskScheduler().also { it.setVirtualThreads(true) }

    @Bean
    fun clock(): Clock = Clock.systemUTC()

}