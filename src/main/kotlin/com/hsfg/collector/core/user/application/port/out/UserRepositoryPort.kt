package com.hsfg.collector.core.user.application.port.out

import com.hsfg.collector.core.user.domain.User
import com.hsfg.collector.core.user.domain.UserId

interface UserRepositoryPort {

    fun save(user: User)

    fun getById(id: UserId): User?
}