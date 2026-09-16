package com.hsfg.collector.core.user.domain

import com.hsfg.collector.core.interaction.domain.ChatId

data class ChatAuthority(val chatId: ChatId, var authState: String, var token: String?)
