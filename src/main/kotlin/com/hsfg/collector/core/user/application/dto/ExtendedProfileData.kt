package com.hsfg.collector.core.user.application.dto

import com.hsfg.collector.core.user.domain.UserId

data class ExtendedProfileData(
    val userId: UserId,
    val registrationCode: String,
)