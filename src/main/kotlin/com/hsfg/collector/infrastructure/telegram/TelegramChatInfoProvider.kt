package com.hsfg.collector.infrastructure.telegram

import com.hsfg.collector.core.interaction.application.port.out.ChatInfoProviderPort
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.ChatInfo
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.ChatFullInfo
import com.pengrad.telegrambot.request.GetChat
import org.springframework.stereotype.Component


@Component
class TelegramChatInfoProvider(
    private val bot: TelegramBot
) : ChatInfoProviderPort {

    override fun getChatInfo(chatId: ChatId): ChatInfo {
        val request = GetChat(chatId.value.toLong())
        val response = bot.execute(request)

        if (response.isOk.not()) {
            error("Failed to get chat info for chatId: ${chatId.value}, error: ${response.description()}")
        }

        return response.chat().toDomain()
    }
}

private fun ChatFullInfo.toDomain() = ChatInfo(
    chatId = ChatId(this.id().toString()),
    firstName = this.firstName(),
    username = this.username()
)
