package com.hsfg.collector.infrastructure.persistence.entity

import jakarta.persistence.*
import org.springframework.data.domain.Persistable
import java.util.*

@Embeddable
data class ConnectionDetailsEntityId(

    @Column(nullable = false)
    var userId: String,

    @Column(nullable = false)
    var detailsId: UUID,
)

@Entity
@Table(name = "connection_details")
class ConnectionDetailsEntity(

    @EmbeddedId
    var entityId: ConnectionDetailsEntityId,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, columnDefinition = "text")
    var details: String,

    @Column(nullable = false, columnDefinition = "text")
    var notes: String,

    @Transient
    var new: Boolean = false,
) : Persistable<ConnectionDetailsEntityId> {

    override fun getId() = entityId

    override fun isNew() = new
}