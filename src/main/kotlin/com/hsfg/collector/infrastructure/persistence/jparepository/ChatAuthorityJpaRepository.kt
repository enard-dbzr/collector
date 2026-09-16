package com.hsfg.collector.infrastructure.persistence.jparepository

import com.hsfg.collector.infrastructure.persistence.entity.ChatAuthorityEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ChatAuthorityJpaRepository : JpaRepository<ChatAuthorityEntity, String> {

}