@file:OptIn(ExperimentalUuidApi::class)

package com.hsfg.collector.infrastructure.persistence

import com.hsfg.collector.core.connection.application.port.ConnectionDetailsRepositoryPort
import com.hsfg.collector.core.connection.domain.ConnectionDetails
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.utils.exception.IdCollisionException
import com.hsfg.collector.infrastructure.persistence.entity.ConnectionDetailsEntity
import com.hsfg.collector.infrastructure.persistence.entity.ConnectionDetailsEntityId
import com.hsfg.collector.infrastructure.persistence.jparepository.ConnectionDetailsJpaRepository
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Repository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@Repository
class DbConnectionDetailsRepository(
    private val jpaRepository: ConnectionDetailsJpaRepository,
) : ConnectionDetailsRepositoryPort {

    override fun create(connection: ConnectionDetails) {
        val entity = connection.toEntity()

        try {
            jpaRepository.saveAndFlush(entity)
        } catch (e: DataIntegrityViolationException) {
            if (e.isUniqueConstraintViolation()) {
                throw IdCollisionException("(detailsId=${connection.detailsId}, userId=${connection.userId})")
            }
            throw e
        }
    }

    @Transactional
    override fun get(userId: UserId, connectionId: Uuid): ConnectionDetails? {
        return jpaRepository.findById(
            ConnectionDetailsEntityId(
                userId = userId.value,
                detailsId = connectionId.toJavaUuid(),
            )
        ).orElse(null)?.toDomain()
    }

    @Transactional
    override fun update(connection: ConnectionDetails) {
        jpaRepository.save(connection.toEntity())
    }

    @Transactional
    override fun getUserConnections(userId: String): List<ConnectionDetails> {
        return jpaRepository.findAllByIdUserId(userId)
            .map(ConnectionDetailsEntity::toDomain)
    }


}

private fun ConnectionDetails.toEntity() = ConnectionDetailsEntity(
    entityId = ConnectionDetailsEntityId(
        userId = userId.value,
        detailsId = detailsId.toJavaUuid(),
    ),
    name = name,
    details = details,
    notes = notes,
)

private fun ConnectionDetailsEntity.toDomain() = ConnectionDetails(
    userId = UserId(entityId.userId),
    detailsId = entityId.detailsId.toKotlinUuid(),
    name = name,
    details = details,
    notes = notes,
)