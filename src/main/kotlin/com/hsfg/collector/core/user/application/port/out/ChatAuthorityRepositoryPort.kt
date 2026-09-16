package com.hsfg.collector.core.user.application.port.out

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.domain.ChatAuthority

interface ChatAuthorityRepositoryPort {

    fun save(authority: ChatAuthority)

    fun get(chatId: ChatId): ChatAuthority?
}