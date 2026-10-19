package com.hsfg.collector.infrastructure.persistence.entity

import jakarta.persistence.*
import org.springframework.data.domain.Persistable

@Entity
@Table(name = "invitations")
class InvitationEntity(
    @Id
    var code: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_id", nullable = false)
    var inviter: UserEntity,

    @Column(nullable = false, columnDefinition = "text")
    var notes: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_id", unique = true)
    var invitee: UserEntity?,

    @Transient
    var new: Boolean = false,
) : Persistable<String> {

    override fun isNew() = new

    override fun getId() = code
}