package com.hsfg.collector.core.user.application.dto

import com.hsfg.collector.core.user.domain.UserId

data class AuthenticationResult(
    val token: String,
    val userId: UserId,
    val isRegistered: Boolean,
)
