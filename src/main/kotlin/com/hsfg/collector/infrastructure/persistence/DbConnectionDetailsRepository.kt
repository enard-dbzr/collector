@file:OptIn(ExperimentalUuidApi::class)

package com.hsfg.collector.infrastructure.persistence

import com.hsfg.collector.core.connection.application.port.ConnectionDetailsRepositoryPort
import com.hsfg.collector.core.connection.domain.ConnectionDetails
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.utils.exception.IdCollisionException
import com.hsfg.collector.infrastructure.persistence.entity.ConnectionDetailsEntity
import com.hsfg.collector.infrastructure.persistence.entity.ConnectionDetailsEntityId
import com.hsfg.collector.infrastructure.persistence.jparepository.ConnectionDetailsJpaRepository
import jakarta.persistence.EntityManager
import org.hibernate.exception.ConstraintViolationException
import org.springframework.stereotype.Repository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@Repository
class DbConnectionDetailsRepository(
    private val jpaRepository: ConnectionDetailsJpaRepository,
    private val entityManager: EntityManager,
) : ConnectionDetailsRepositoryPort {

    override fun create(connection: ConnectionDetails) {
        val entity = connection.toEntity()

        try {
            entityManager.persist(entity)
            entityManager.flush()
        } catch (_: ConstraintViolationException) {
            throw IdCollisionException("(detailsId=${connection.detailsId}, userId=${connection.userId})")
        }
    }

    override fun get(userId: UserId, connectionId: Uuid): ConnectionDetails? {
        return jpaRepository.findById(
            ConnectionDetailsEntityId(
                userId = userId.value,
                detailsId = connectionId.toJavaUuid(),
            )
        ).orElse(null)?.toDomain()
    }

    override fun update(connection: ConnectionDetails) {
        jpaRepository.save(connection.toEntity())
    }

    override fun getUserConnections(userId: String): List<ConnectionDetails> {
        return jpaRepository.findAllByIdUserId(userId)
            .map(ConnectionDetailsEntity::toDomain)
    }


}

private fun ConnectionDetails.toEntity() = ConnectionDetailsEntity(
    id = ConnectionDetailsEntityId(
        userId = userId.value,
        detailsId = detailsId.toJavaUuid(),
    ),
    name = name,
    details = details,
    notes = notes,
)

private fun ConnectionDetailsEntity.toDomain() = ConnectionDetails(
    userId = UserId(id.userId),
    detailsId = id.detailsId.toKotlinUuid(),
    name = name,
    details = details,
    notes = notes,
)