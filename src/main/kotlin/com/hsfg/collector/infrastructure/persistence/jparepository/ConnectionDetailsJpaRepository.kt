package com.hsfg.collector.infrastructure.persistence.jparepository

import com.hsfg.collector.infrastructure.persistence.entity.ConnectionDetailsEntity
import com.hsfg.collector.infrastructure.persistence.entity.ConnectionDetailsEntityId
import org.springframework.data.jpa.repository.JpaRepository

interface ConnectionDetailsJpaRepository : JpaRepository<ConnectionDetailsEntity, ConnectionDetailsEntityId> {
    fun findAllByIdUserId(userId: String): List<ConnectionDetailsEntity>
}