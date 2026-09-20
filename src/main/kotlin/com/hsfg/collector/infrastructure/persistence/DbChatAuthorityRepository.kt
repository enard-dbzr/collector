package com.hsfg.collector.infrastructure.persistence

import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.user.application.port.out.ChatAuthorityRepositoryPort
import com.hsfg.collector.core.user.domain.ChatAuthority
import com.hsfg.collector.infrastructure.persistence.entity.ChatAuthorityEntity
import com.hsfg.collector.infrastructure.persistence.jparepository.ChatAuthorityJpaRepository
import org.springframework.stereotype.Repository

@Repository
class DbChatAuthorityRepository(
    private val jpaRepository: ChatAuthorityJpaRepository,
) : ChatAuthorityRepositoryPort {

    override fun save(authority: ChatAuthority) {
        jpaRepository.saveAndFlush(ChatAuthorityEntity().fillFromDomain(authority))
    }

    override fun get(chatId: ChatId): ChatAuthority? {
        return jpaRepository.findById(chatId.value).orElse(null)?.toDomain()
    }

    override fun getByAuthState(authState: String): ChatAuthority? {
        return jpaRepository.getByState(authState)?.toDomain()
    }
}