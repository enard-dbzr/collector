package com.hsfg.collector.infrastructure.persistence

import com.hsfg.collector.core.invitation.application.port.out.InvitationRepository
import com.hsfg.collector.core.invitation.domain.Invitation
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.utils.exception.IdCollisionException
import com.hsfg.collector.infrastructure.persistence.entity.InvitationEntity
import com.hsfg.collector.infrastructure.persistence.jparepository.InvitationJpaRepository
import com.hsfg.collector.infrastructure.persistence.jparepository.UserJpaRepository
import jakarta.persistence.EntityManager
import org.hibernate.exception.ConstraintViolationException
import org.springframework.stereotype.Repository

@Repository
class DbInvitationRepository(
    private val jpaRepository: InvitationJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val entityManager: EntityManager,
) : InvitationRepository {

    override fun create(invitation: Invitation) {
        val entity = invitation.toEntity(userJpaRepository)

        try {
            entityManager.persist(entity)
            entityManager.flush()
        } catch (_: ConstraintViolationException) {
            throw IdCollisionException("code=${invitation.code}")
        }
    }

    override fun getByCode(code: String): Invitation? {
        return jpaRepository.findById(code)
            .orElse(null)
            ?.toDomain()
    }

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