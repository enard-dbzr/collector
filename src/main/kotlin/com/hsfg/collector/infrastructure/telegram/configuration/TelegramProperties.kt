package com.hsfg.collector.infrastructure.telegram.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import java.time.Duration
import java.time.temporal.ChronoUnit


@ConfigurationProperties(prefix = "app.telegram")
data class TelegramProperties(

    val url: String,

    val token: String,

    @DurationUnit(ChronoUnit.MILLIS)
    val updateListenerSleep: Duration = Duration.ofMinutes(2),

    val debug: Boolean = false
)
