package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.user.application.dto.AuthenticationResult
import com.hsfg.collector.core.user.application.event.UserRegisteredEvent
import com.hsfg.collector.core.user.application.port.out.UserRepositoryPort
import com.hsfg.collector.core.user.domain.User
import com.hsfg.collector.core.user.domain.UserId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.casbin.casdoor.exception.AuthException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.casbin.casdoor.service.AuthService as CasdoorAuthService

@Service
class AuthenticationService(
    private val casdoorAuthService: CasdoorAuthService,
    private val userRepository: UserRepositoryPort,
    private val eventPublisher: ApplicationEventPublisher,
) {

    companion object {
        val log = KotlinLogging.logger {}
    }

    fun createLoginUrl(redirectUrl: String, state: String): String {
        return casdoorAuthService.getSigninUrl(redirectUrl, state)
    }

    fun authenticate(code: String, state: String): AuthenticationResult {
        val token = casdoorAuthService.getOAuthToken(code, state)
        val casdoorUser = casdoorAuthService.parseJwtToken(token)

        val userId = UserId(casdoorUser.id)
        val isRegistered = userRepository.getById(userId) == null

        if (isRegistered) {
            val user = User(userId)

            userRepository.save(user)

            log.info { "Registered new user with id=${user.id}" }
            eventPublisher.publishEvent(UserRegisteredEvent(user))
        }

        return AuthenticationResult(
            token = token,
            userId = UserId(casdoorUser.id),
            isRegistered = isRegistered
        )
    }

    fun isAuthenticated(token: String) = try {
        casdoorAuthService.parseJwtToken(token)
        true
    } catch (_: AuthException) {
        false
    }

}