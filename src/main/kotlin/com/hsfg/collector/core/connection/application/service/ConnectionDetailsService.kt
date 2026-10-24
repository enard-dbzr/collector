package com.hsfg.collector.core.connection.application.service

import com.hsfg.collector.core.connection.application.dto.ConnectionDetailsDto
import com.hsfg.collector.core.connection.application.event.ConnectionsUpdatedEvent
import com.hsfg.collector.core.connection.application.exception.ConnectionDetailsNotFound
import com.hsfg.collector.core.connection.application.port.ConnectionDetailsRepositoryPort
import com.hsfg.collector.core.connection.domain.ConnectionDetails
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.utils.exception.IdCollisionException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.uuid.ExperimentalUuidApi

@Service
class ConnectionDetailsService(
    private val repository: ConnectionDetailsRepositoryPort,
    private val eventPublisher: ApplicationEventPublisher,
) {

    companion object {
        val logger = KotlinLogging.logger { }
    }

    /**
     * @throws IdCollisionException when a connection with the same id already exists for the user
     */
    fun create(connectionDto: ConnectionDetailsDto) {
        val connection = connectionDto.toDomain()
        repository.create(connection)

        logger.info { "Created connection details for user with id=${connection.userId}" }
        eventPublisher.publishEvent(ConnectionsUpdatedEvent(connection.userId))
    }

    fun getUserConnections(userId: UserId): List<ConnectionDetailsDto> {
        return repository.getUserConnections(userId.value).map { it.toDto() }
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

        eventPublisher.publishEvent(ConnectionsUpdatedEvent(connection.userId))
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

@OptIn(ExperimentalUuidApi::class)
private fun ConnectionDetails.toDto() = ConnectionDetailsDto(
    userId = this.userId,
    detailsId = this.detailsId,
    name = this.name,
    details = this.details,
    notes = this.notes,
)

private fun ConnectionDetails.fillUpdates(connectionDto: ConnectionDetailsDto) {
    this.name = connectionDto.name
    this.details = connectionDto.details
    this.notes = connectionDto.notes
}