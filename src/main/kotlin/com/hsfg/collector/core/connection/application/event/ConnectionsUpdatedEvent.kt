package com.hsfg.collector.core.connection.application.event

import com.hsfg.collector.core.user.domain.UserId

data class ConnectionsUpdatedEvent(
    val userId: UserId,
)