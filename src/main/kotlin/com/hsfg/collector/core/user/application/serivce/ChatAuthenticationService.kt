package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.application.config.ChatAuthenticationProperties
import com.hsfg.collector.core.user.application.port.out.ChatAuthorityRepositoryPort
import com.hsfg.collector.core.user.domain.ChatAuthority
import org.casbin.casdoor.service.AuthService
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Service
class ChatAuthenticationService(
    private val properties: ChatAuthenticationProperties,
    private val authorityRepositoryPort: ChatAuthorityRepositoryPort,
    private val casdoorAuthService: AuthService,
) {

    @OptIn(ExperimentalUuidApi::class)
    fun createLoginUrl(chatId: ChatId): String {
        val authority = ChatAuthority(
            chatId = chatId,
            authState = Uuid.generateV7().toString(),
            token = null
        )

        authorityRepositoryPort.save(authority)

        return casdoorAuthService.getSigninUrl(properties.callback, authority.authState)
    }

    fun isAuthenticated(chatId: ChatId): Boolean {
        val authority = authorityRepositoryPort.get(chatId)
        return authority?.token != null
    }
}