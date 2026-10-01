package com.hsfg.collector.infrastructure.persistence.entity

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.domain.ChatAuthority
import com.hsfg.collector.core.user.domain.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "chat_authorities")
class ChatAuthorityEntity {

    @Id
    private lateinit var chatId: String

    @Column(unique = true, nullable = false)
    private lateinit var state: String

    @Column(columnDefinition = "text")
    private var token: String? = null

    private var userId: String? = null

    fun fillFromDomain(chatAuthority: ChatAuthority) = apply {
        this.chatId = chatAuthority.chatId.value
        this.state = chatAuthority.authState
        this.token = chatAuthority.token
        this.userId = chatAuthority.userId?.value
    }

    fun toDomain() = ChatAuthority(
        ChatId(chatId),
        state,
        token,
        userId?.let { UserId(it) }
    )
}