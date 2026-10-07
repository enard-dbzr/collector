package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.application.config.ChatAuthenticationProperties
import com.hsfg.collector.core.user.application.dto.UserProfile
import com.hsfg.collector.core.user.application.event.ChatAuthorizedEvent
import com.hsfg.collector.core.user.application.exception.ChatUnauthorizedException
import com.hsfg.collector.core.user.application.port.out.ChatAuthorityRepositoryPort
import com.hsfg.collector.core.user.domain.ChatAuthority
import com.hsfg.collector.core.user.domain.UserId
import org.casbin.casdoor.exception.AuthException
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
        )

        authorityRepositoryPort.save(authority)

        return casdoorAuthService.getSigninUrl(properties.callback, authority.authState)
    }

    /**
     * @throws ChatUnauthorizedException if the token is invalid
     */
    fun login(authState: String, code: String) {
        val authority = authorityRepositoryPort.getByAuthState(authState)
            ?: error("Can not find user with authState $authState")

        val token = casdoorAuthService.getOAuthToken(code, authState)
        val user = parseToken(authority.chatId, token)

        authority.token = token
        authority.userId = UserId(user.id)

        authorityRepositoryPort.save(authority)
        eventPublisher.publishEvent(ChatAuthorizedEvent(authority.chatId))
    }

    fun isAuthenticated(chatId: ChatId): Boolean {
        val authority = authorityRepositoryPort.get(chatId)

        authority?.token ?: return false

        try {
            casdoorAuthService.parseJwtToken(authority.token)
        } catch (_: AuthException) {
            return false
        }

        return true
    }

    /**
     * @throws ChatUnauthorizedException if the token is invalid
     */
    fun getLocalUser(chatId: ChatId): UserProfile {
        val authority = authorityRepositoryPort.get(chatId)

        val token = authority?.token ?: throw ChatUnauthorizedException(chatId)

        return parseToken(chatId, token).toProfile()
    }

    private fun parseToken(chatId: ChatId, token: String) = try {
        casdoorAuthService.parseJwtToken(token)
    } catch (_: AuthException) {
        throw ChatUnauthorizedException(chatId)
    }
}