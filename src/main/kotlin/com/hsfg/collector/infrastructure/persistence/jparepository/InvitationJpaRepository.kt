package com.hsfg.collector.infrastructure.persistence.jparepository

import com.hsfg.collector.infrastructure.persistence.entity.InvitationEntity
import com.hsfg.collector.infrastructure.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InvitationJpaRepository : JpaRepository<InvitationEntity, String> {

    fun getByInvitee(invitee: UserEntity): InvitationEntity?
}