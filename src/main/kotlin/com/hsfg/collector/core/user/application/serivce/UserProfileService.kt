package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.user.application.dto.UserProfile
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.infrastructure.client.casdoor.custom.CasdoorUserService
import org.casbin.casdoor.entity.User
import org.springframework.stereotype.Service

@Service
class UserProfileService(
    private val userService: CasdoorUserService,
) {

    fun getProfile(userId: UserId): UserProfile? {
        return userService.getUserById(userId.value)?.toProfile()
    }
}

fun User.toProfile() = UserProfile(
    id = UserId(this.id!!),
    subject = "${this.owner}/${this.name}",
)
