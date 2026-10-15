package com.hsfg.collector.core.invitation.domain

import com.hsfg.collector.core.user.domain.UserId

data class Invitation(
    val inviter: UserId,
    var notes: String,
    val code: String,
    var invitee: UserId? = null,
)
