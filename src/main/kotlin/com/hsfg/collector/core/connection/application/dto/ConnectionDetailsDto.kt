package com.hsfg.collector.core.connection.application.dto

import com.hsfg.collector.core.user.domain.UserId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ConnectionDetailsDto(
    val userId: UserId,
    val detailsId: Uuid,
    val name: String,
    val details: String,
    val notes: String,
) {
    constructor(
        userId: UserId,
        name: String,
        details: String,
        notes: String,
    ) : this(
        userId = userId,
        detailsId = Uuid.generateV7(),
        name = name,
        details = details,
        notes = notes,
    )
}
