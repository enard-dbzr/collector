package com.hsfg.collector.core.connection.domain

import com.hsfg.collector.core.user.domain.UserId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ConnectionDetails(
    val userId: UserId,
    val detailsId: Uuid,
    var name: String,
    var details: String,
    var notes: String,
)
