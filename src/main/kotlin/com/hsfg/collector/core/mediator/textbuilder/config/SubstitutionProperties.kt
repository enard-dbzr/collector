package com.hsfg.collector.core.mediator.textbuilder.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.text")
class SubstitutionProperties(
    val helpChatLink: String
)