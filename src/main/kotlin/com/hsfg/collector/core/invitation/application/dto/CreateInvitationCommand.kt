package com.hsfg.collector.core.invitation.application.dto

import com.hsfg.collector.core.user.domain.UserId

data class CreateInvitationCommand(
    val inviter: UserId,
    val notes: String,
)