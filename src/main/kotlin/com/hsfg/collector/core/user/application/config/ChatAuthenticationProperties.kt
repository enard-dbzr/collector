package com.hsfg.collector.core.user.application.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "app.chat-authentication")
data class ChatAuthenticationProperties(

    val callback: String,

)
