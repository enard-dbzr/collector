package com.hsfg.collector.core.mediator.notification

import com.hsfg.collector.core.user.domain.UserId

data class UserRegisteredNotification(val userId: UserId) : ChatNotification
