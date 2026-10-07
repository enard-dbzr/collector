package com.hsfg.collector.infrastructure.persistence.entity

import jakarta.persistence.*
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
    var id: ConnectionDetailsEntityId,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false, columnDefinition = "text")
    var details: String,

    @Column(nullable = false, columnDefinition = "text")
    var notes: String,
)