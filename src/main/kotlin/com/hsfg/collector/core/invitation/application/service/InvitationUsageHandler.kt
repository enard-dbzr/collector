package com.hsfg.collector.core.invitation.application.service

import com.hsfg.collector.core.user.application.event.UserRegisteredEvent
import com.hsfg.collector.core.user.application.serivce.UserProfileService
import org.springframework.context.event.EventListener
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
class InvitationUsageHandler(
    private val invitationService: InvitationService,
    private val userProfileService: UserProfileService,
) {

    @Order(1)
    @EventListener
    fun onUserRegistered(event: UserRegisteredEvent) {
        val extendedProfileData = userProfileService.getExtendedProfileData(event.user.id)
            ?: error("Extended profile data is null")

        if (extendedProfileData.registrationCode.isBlank()) {
            return
        }

        invitationService.registerInvitee(
            extendedProfileData.registrationCode,
            event.user.id
        )
    }
}