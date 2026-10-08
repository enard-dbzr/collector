package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.user.application.dto.AuthenticationResult
import com.hsfg.collector.core.user.domain.UserId
import org.casbin.casdoor.exception.AuthException
import org.springframework.stereotype.Service
import org.casbin.casdoor.service.AuthService as CasdoorAuthService

@Service
class AuthenticationService(
    private val casdoorAuthService: CasdoorAuthService,
) {

    fun createLoginUrl(redirectUrl: String, state: String): String {
        return casdoorAuthService.getSigninUrl(redirectUrl, state)
    }

    fun authenticate(code: String, state: String): AuthenticationResult {
        val token = casdoorAuthService.getOAuthToken(code, state)
        val casdoorUser = casdoorAuthService.parseJwtToken(token)

        return AuthenticationResult(
            token = token,
            userId = UserId(casdoorUser.id ?: error("User id is null")),
            isRegistered = false
        )
    }

    fun isAuthenticated(token: String) = try {
        casdoorAuthService.parseJwtToken(token)
        true
    } catch (_: AuthException) {
        false
    }

}