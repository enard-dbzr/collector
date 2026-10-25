package com.hsfg.collector.core.mediator.textbuilder

import com.hsfg.collector.core.invitation.application.service.InvitationService
import com.hsfg.collector.core.user.application.serivce.UserProfileService
import com.hsfg.collector.core.user.domain.UserId
import org.springframework.stereotype.Component

@Component
class UserProfileTextBuilder(
    private val userProfileService: UserProfileService,
    private val invitationService: InvitationService,
) {

    fun buildProfile(userId: UserId, header: String): String {
        val profile = userProfileService.getProfile(userId) ?: error("profile not found")
        val invitationRecord = invitationService.findInviter(userId)

        return buildString {
            appendLine(header)
            appendLine()
            appendLine("Имя: ${profile.displayName}")
            appendLine("Email: ${profile.email}")
            appendLine("ID: <code>${profile.id.value}</code>")

            if (invitationRecord != null) {
                appendLine()
                appendLine("Приглашён пользователем: <code>${invitationRecord.inviter.value}</code>")

                if (invitationRecord.notes.isNotBlank()) {
                    appendLine("Заметки: ${invitationRecord.notes}")
                }
            }
        }.trimEnd()
    }
}