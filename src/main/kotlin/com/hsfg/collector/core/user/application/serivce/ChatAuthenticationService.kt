package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.application.event.ChatAuthorizedEvent
import com.hsfg.collector.core.user.application.config.ChatAuthenticationProperties
import com.hsfg.collector.core.user.application.port.out.ChatAuthorityRepositoryPort
import com.hsfg.collector.core.user.domain.ChatAuthority
import org.casbin.casdoor.service.AuthService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Service
class ChatAuthenticationService(
    private val properties: ChatAuthenticationProperties,
    private val authorityRepositoryPort: ChatAuthorityRepositoryPort,
    private val casdoorAuthService: AuthService,
    private val eventPublisher: ApplicationEventPublisher,
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

    fun login(authState: String, code: String) {
        val authority = authorityRepositoryPort.getByAuthState(authState)
            ?: error("Can not find user with authState $authState")

        val token = casdoorAuthService.getOAuthToken(code, authState)

        authority.token = token

        authorityRepositoryPort.save(authority)
        eventPublisher.publishEvent(ChatAuthorizedEvent(authority.chatId))
    }

    fun isAuthenticated(chatId: ChatId): Boolean {
        val authority = authorityRepositoryPort.get(chatId)
        return authority?.token != null
    }
}