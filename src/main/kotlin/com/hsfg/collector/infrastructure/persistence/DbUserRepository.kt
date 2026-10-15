package com.hsfg.collector.infrastructure.persistence

import com.hsfg.collector.core.user.application.port.out.UserRepositoryPort
import com.hsfg.collector.core.user.domain.User
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.infrastructure.persistence.entity.UserEntity
import com.hsfg.collector.infrastructure.persistence.jparepository.UserJpaRepository
import org.springframework.stereotype.Repository

@Repository
class DbUserRepository(
    private val jpaRepository: UserJpaRepository,
) : UserRepositoryPort {

    override fun save(user: User) {
        jpaRepository.save(UserEntity(user.id.value))
    }

    override fun getById(id: UserId): User? {
        return jpaRepository.findById(id.value)
            .map {
                User(
                    id = UserId(it.userId)
                )
            }
            .orElse(null)
    }


}