package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.user.application.dto.UserProfile
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.infrastructure.client.casdoor.custom.CasdoorUserService
import org.casbin.casdoor.entity.User
import org.springframework.stereotype.Service
import org.casbin.casdoor.service.AuthService as CasdoorAuthService

@Service
class UserProfileService(
    private val casdoorUserService: CasdoorUserService,
    private val casdoorAuthService: CasdoorAuthService
) {

    fun getProfile(userId: UserId): UserProfile? {
        return casdoorUserService.getUserById(userId.value)?.toProfile()
    }

    fun parseLocalProfile(token: String): UserProfile {
        return casdoorAuthService.parseJwtToken(token).toProfile()
    }
}

private fun User.toProfile() = UserProfile(
    id = UserId(this.id),
    subject = "${this.owner}/${this.name}",
)
