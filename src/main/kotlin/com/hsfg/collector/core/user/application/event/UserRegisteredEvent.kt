package com.hsfg.collector.core.user.application.event

import com.hsfg.collector.core.user.domain.User

data class UserRegisteredEvent(val user: User)
