package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.user.application.dto.UserProfile
import com.hsfg.collector.core.user.domain.UserId
import org.casbin.casdoor.entity.User
import org.springframework.stereotype.Service
import org.casbin.casdoor.service.AuthService as CasdoorAuthService
import org.casbin.casdoor.service.UserService as CasdoorUserService

@Service
class UserProfileService(
    private val casdoorUserService: CasdoorUserService,
    private val casdoorAuthService: CasdoorAuthService,
) {

    fun getProfile(userId: UserId): UserProfile? {
        return casdoorUserService.getUserByUserId(userId.value)?.toProfile()
    }

    fun parseLocalProfile(token: String): UserProfile {
        return casdoorAuthService.parseJwtToken(token).toProfile()
    }
}

private fun User.toProfile() = UserProfile(
    id = UserId(this.id),
    subject = "${this.owner}/${this.name}",
)
