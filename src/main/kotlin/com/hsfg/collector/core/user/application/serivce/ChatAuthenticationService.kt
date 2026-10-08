package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.application.config.ChatAuthenticationProperties
import com.hsfg.collector.core.user.application.dto.UserProfile
import com.hsfg.collector.core.user.application.event.ChatAuthorizedEvent
import com.hsfg.collector.core.user.application.exception.ChatUnauthorizedException
import com.hsfg.collector.core.user.application.port.out.ChatAuthorityRepositoryPort
import com.hsfg.collector.core.user.domain.ChatAuthority
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Service
class ChatAuthenticationService(
    private val properties: ChatAuthenticationProperties,
    private val authorityRepositoryPort: ChatAuthorityRepositoryPort,
    private val authenticationService: AuthenticationService,
    private val userProfileService: UserProfileService,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @OptIn(ExperimentalUuidApi::class)
    fun createLoginUrl(chatId: ChatId): String {
        val authority = ChatAuthority(
            chatId = chatId,
            authState = Uuid.generateV7().toString(),
        )

        authorityRepositoryPort.save(authority)

        return authenticationService.createLoginUrl(properties.callback, authority.authState)
    }

    fun login(authState: String, code: String) {
        val authority = authorityRepositoryPort.getByAuthState(authState)
            ?: error("Can not find user with authState $authState")

        val authResult = authenticationService.authenticate(code, authState)

        authority.token = authResult.token
        authority.userId = authResult.userId

        authorityRepositoryPort.save(authority)
        eventPublisher.publishEvent(ChatAuthorizedEvent(authority.chatId))
    }

    fun isAuthenticated(chatId: ChatId): Boolean {
        val authority = authorityRepositoryPort.get(chatId)

        val token = authority?.token ?: return false

        return authenticationService.isAuthenticated(token)
    }

    /**
     * @throws ChatUnauthorizedException if the token is invalid
     */
    fun getLocalUser(chatId: ChatId): UserProfile {
        val authority = authorityRepositoryPort.get(chatId)

        val token = authority?.token ?: throw ChatUnauthorizedException(chatId)

        return userProfileService.parseLocalProfile(token)
    }
}