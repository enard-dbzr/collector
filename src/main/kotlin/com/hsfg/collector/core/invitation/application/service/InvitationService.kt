package com.hsfg.collector.core.invitation.application.service

import com.hsfg.collector.core.invitation.application.dto.CreateInvitationCommand
import com.hsfg.collector.core.invitation.application.port.out.InvitationRepository
import com.hsfg.collector.core.invitation.domain.Invitation
import com.hsfg.collector.core.user.domain.UserId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class InvitationService(
    private val repository: InvitationRepository,
) {

    companion object {
        val log = KotlinLogging.logger { }

        val CODE_ALLOWED_CHARS = ('A'..'Z') + ('a'..'z')
        const val CODE_LENGTH = 12
    }

    private val secureRandom = SecureRandom()

    fun create(command: CreateInvitationCommand): String {
        val invitation = Invitation(
            inviter = command.inviter,
            notes = command.notes,
            code = generateCode()
        )

        repository.create(invitation)

        return invitation.code
    }

    fun registerInvitee(code: String, inviteeId: UserId) {
        val invitation = repository.getByCode(code)
            ?: error("Invitation with code=$code not found")

        if (invitation.invitee != null) {
            error("Invitation with code=$code has already been used by invitee=${invitation.invitee}")
        }

        invitation.invitee = inviteeId

        repository.update(invitation)

        log.info { "Invitee with id=$inviteeId registered" }
    }

    private fun generateCode(): String =
        buildString(CODE_LENGTH) {
            repeat(CODE_LENGTH) {
                append(CODE_ALLOWED_CHARS[secureRandom.nextInt(CODE_ALLOWED_CHARS.size)])
            }
        }
}