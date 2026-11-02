package com.hsfg.collector.core.invitation.application.dto

import com.hsfg.collector.core.user.domain.UserId

data class InvitationRecord(
    val inviter: UserId,
    val invitee: UserId,
    val notes: String
)