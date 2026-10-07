package com.hsfg.collector.core.connection.application.port

import com.hsfg.collector.core.connection.domain.ConnectionDetails
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.utils.exception.IdCollisionException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface ConnectionDetailsRepositoryPort {

    /**
     * @throws IdCollisionException when a connection with the same id already exists for the user
     */
    fun create(connection: ConnectionDetails)

    @OptIn(ExperimentalUuidApi::class)
    fun get(userId: UserId, connectionId: Uuid): ConnectionDetails?

    fun update(connection: ConnectionDetails)

    fun getUserConnections(userId: String): List<ConnectionDetails>
}