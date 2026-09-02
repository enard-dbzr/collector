package com.hsfg.collector.infrastructure.telegram.configuration

import com.pengrad.telegrambot.TelegramBot
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TelegramConfiguration {
    @Bean
    fun telegramBot(properties: TelegramProperties): TelegramBot {
        val builder = TelegramBot.Builder(properties.token)
            .apiUrl(properties.url)
            .updateListenerSleep(properties.updateListenerSleep.toMillis())

        if (properties.debug) {
            builder.debug()
        }

        return builder.build()
    }
}
