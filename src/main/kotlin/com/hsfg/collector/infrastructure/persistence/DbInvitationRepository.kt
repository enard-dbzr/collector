package com.hsfg.collector.infrastructure.persistence

import com.hsfg.collector.core.invitation.application.port.out.InvitationRepository
import com.hsfg.collector.core.invitation.domain.Invitation
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.utils.exception.IdCollisionException
import com.hsfg.collector.infrastructure.persistence.entity.InvitationEntity
import com.hsfg.collector.infrastructure.persistence.jparepository.InvitationJpaRepository
import com.hsfg.collector.infrastructure.persistence.jparepository.UserJpaRepository
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Repository

@Repository
class DbInvitationRepository(
    private val jpaRepository: InvitationJpaRepository,
    private val userJpaRepository: UserJpaRepository,
) : InvitationRepository {

    override fun create(invitation: Invitation) {
        val entity = invitation.toEntity(userJpaRepository).apply { new = true }

        try {
            jpaRepository.saveAndFlush(entity)
        } catch (e: DataIntegrityViolationException) {
            if (e.isUniqueConstraintViolation()) {
                throw IdCollisionException("code=${invitation.code}")
            }
            throw e
        }

    }

    @Transactional
    override fun getByCode(code: String): Invitation? {
        return jpaRepository.findById(code)
            .orElse(null)
            ?.toDomain()
    }

    @Transactional
    override fun update(invitation: Invitation) {
        jpaRepository.save(invitation.toEntity(userJpaRepository))
    }

}

private fun Invitation.toEntity(userJpaRepository: UserJpaRepository) = InvitationEntity(
    code = code,
    inviter = userJpaRepository.getReferenceById(inviter.value),
    notes = notes,
    invitee = invitee?.let { userJpaRepository.getReferenceById(it.value) }
)

private fun InvitationEntity.toDomain() = Invitation(
    inviter = UserId(inviter.userId),
    notes = notes,
    code = code,
    invitee = invitee?.let { UserId(it.userId) }
)