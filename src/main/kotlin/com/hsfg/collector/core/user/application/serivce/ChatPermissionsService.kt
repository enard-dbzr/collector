package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.domain.UserPermission
import org.springframework.stereotype.Service

@Service
class ChatPermissionsService(
    private val chatAuthenticationService: ChatAuthenticationService,
    private val userProfileService: UserProfileService,
    private val permissionsService: PermissionsService,
) {

    fun check(chatId: ChatId, permission: UserPermission): Boolean {
        val localUser = chatAuthenticationService.getLocalUser(chatId)
        val actualUser = userProfileService.getProfile(localUser.id)
            ?: error("User with id ${localUser.id} not found")

        return permissionsService.check(actualUser.subject, permission)
    }
}