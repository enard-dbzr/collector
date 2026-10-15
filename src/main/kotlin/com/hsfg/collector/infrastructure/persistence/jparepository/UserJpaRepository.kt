package com.hsfg.collector.infrastructure.persistence.jparepository

import com.hsfg.collector.infrastructure.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, String>