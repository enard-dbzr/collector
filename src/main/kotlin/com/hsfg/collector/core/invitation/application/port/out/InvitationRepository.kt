package com.hsfg.collector.core.invitation.application.port.out

import com.hsfg.collector.core.invitation.domain.Invitation

interface InvitationRepository {

    fun create(invitation: Invitation)

    fun getByCode(code: String): Invitation?

    fun update(invitation: Invitation)
}