package com.hsfg.collector.infrastructure.persistence

import com.hsfg.collector.core.notification.application.port.out.NotificationAssignmentRepositoryPort
import com.hsfg.collector.core.notification.domain.NotificationAssignment
import com.hsfg.collector.core.notification.domain.ToNotifyEventType
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.infrastructure.persistence.entity.NotificationAssignmentEntity
import com.hsfg.collector.infrastructure.persistence.jparepository.NotificationAssignmentJpaRepository
import com.hsfg.collector.infrastructure.persistence.jparepository.UserJpaRepository
import org.springframework.stereotype.Repository

@Repository
class DbNotificationAssignmentRepository(
    private val jpaRepository: NotificationAssignmentJpaRepository,
    private val userJpaRepository: UserJpaRepository,
) : NotificationAssignmentRepositoryPort {

    override fun getByEventType(eventType: ToNotifyEventType): List<NotificationAssignment> {
        return jpaRepository.getAllByEventType(eventType).map { it.toDomain() }
    }

}

private fun NotificationAssignmentEntity.toDomain() = NotificationAssignment(
    userId = UserId(this.user.userId),
    eventType = this.eventType,
)