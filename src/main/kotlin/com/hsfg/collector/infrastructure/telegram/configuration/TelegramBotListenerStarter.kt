package com.hsfg.collector.infrastructure.telegram.configuration

import com.pengrad.telegrambot.TelegramBot
import com.hsfg.collector.infrastructure.telegram.TelegramUpdateController
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class TelegramBotListenerStarter(
    private val bot: TelegramBot,
    private val updateController: TelegramUpdateController
) {

    @PostConstruct
    fun start() {
        bot.setUpdatesListener(updateController)
    }
}
