package com.hsfg.collector.core.user.application.dto

import com.hsfg.collector.core.user.domain.UserId

data class UserProfile(val id: UserId, val subject: String)