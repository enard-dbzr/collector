package com.hsfg.collector.infrastructure.persistence.jparepository

import com.hsfg.collector.core.notification.domain.ToNotifyEventType
import com.hsfg.collector.infrastructure.persistence.entity.NotificationAssignmentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationAssignmentJpaRepository : JpaRepository<NotificationAssignmentEntity, Long> {

    fun getAllByEventType(eventType: ToNotifyEventType): List<NotificationAssignmentEntity>
}