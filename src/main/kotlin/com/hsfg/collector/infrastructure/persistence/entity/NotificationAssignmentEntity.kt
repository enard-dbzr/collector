package com.hsfg.collector.infrastructure.persistence.entity

import com.hsfg.collector.core.notification.domain.ToNotifyEventType
import jakarta.persistence.*

@Entity
@Table(name = "notification_assignments")
class NotificationAssignmentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var eventType: ToNotifyEventType,

    )