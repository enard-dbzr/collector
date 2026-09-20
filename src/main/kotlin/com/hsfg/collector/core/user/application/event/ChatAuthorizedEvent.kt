package com.hsfg.collector.core.user.application.event

import com.hsfg.collector.core.interaction.domain.ChatId

data class ChatAuthorizedEvent(val chatId: ChatId)