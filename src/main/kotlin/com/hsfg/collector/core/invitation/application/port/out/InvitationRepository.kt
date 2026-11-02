package com.hsfg.collector.core.invitation.application.port.out

import com.hsfg.collector.core.invitation.domain.Invitation
import com.hsfg.collector.core.user.domain.UserId

interface InvitationRepository {

    fun create(invitation: Invitation)

    fun getByCode(code: String): Invitation?

    fun getByInvitee(invitee: UserId): Invitation?

    fun update(invitation: Invitation)
}