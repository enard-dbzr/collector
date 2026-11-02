package com.hsfg.collector.core.interaction.application.port.out

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.ChatInfo

interface ChatInfoProviderPort {

    fun getChatInfo(chatId: ChatId): ChatInfo
}