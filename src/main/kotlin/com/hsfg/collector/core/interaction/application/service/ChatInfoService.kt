package com.hsfg.collector.core.interaction.application.service

import com.hsfg.collector.core.interaction.application.port.out.ChatInfoProviderPort
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.ChatInfo
import org.springframework.stereotype.Service

@Service
class ChatInfoService(
    private val chatInfoProviderPort: ChatInfoProviderPort,
) {

    fun getChatInfo(chatId: ChatId): ChatInfo {
        return chatInfoProviderPort.getChatInfo(chatId)
    }
}