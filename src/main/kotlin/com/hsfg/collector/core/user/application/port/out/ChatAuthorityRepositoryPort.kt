package com.hsfg.collector.core.user.application.port.out

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.domain.ChatAuthority
import com.hsfg.collector.core.user.domain.UserId

interface ChatAuthorityRepositoryPort {

    fun save(authority: ChatAuthority)

    fun get(chatId: ChatId): ChatAuthority?

    fun getByAuthState(authState: String): ChatAuthority?

    fun getByUserId(userId: UserId): List<ChatAuthority>
}