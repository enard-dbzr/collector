package com.hsfg.collector.core.connection.application.service

import com.hsfg.collector.core.connection.application.dto.ConnectionDetailsDto
import com.hsfg.collector.core.connection.application.exception.ConnectionDetailsNotFound
import com.hsfg.collector.core.connection.application.port.ConnectionDetailsRepositoryPort
import com.hsfg.collector.core.connection.domain.ConnectionDetails
import com.hsfg.collector.core.utils.exception.IdCollisionException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.uuid.ExperimentalUuidApi

@Service
class ConnectionDetailsService(
    private val repository: ConnectionDetailsRepositoryPort,
) {

    /**
     * @throws IdCollisionException when a connection with the same id already exists for the user
     */
    fun create(connectionDto: ConnectionDetailsDto) {
        repository.create(connectionDto.toDomain())
    }

    /**
     * @throws ConnectionDetailsNotFound when a connection with the given id does not exist for the user
     */
    @Transactional
    @OptIn(ExperimentalUuidApi::class)
    fun update(connectionDto: ConnectionDetailsDto) {
        val connection = repository.get(connectionDto.userId, connectionDto.detailsId)
            ?: throw ConnectionDetailsNotFound(connectionDto.userId, connectionDto.detailsId.toString())

        connection.fillUpdates(connectionDto)

        repository.update(connection)
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun ConnectionDetailsDto.toDomain() = ConnectionDetails(
    userId = this.userId,
    detailsId = this.detailsId,
    name = this.name,
    details = this.details,
    notes = this.notes
)

private fun ConnectionDetails.fillUpdates(connectionDto: ConnectionDetailsDto) {
    this.name = connectionDto.name
    this.details = connectionDto.details
    this.notes = connectionDto.notes
}