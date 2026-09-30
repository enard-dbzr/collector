package com.hsfg.collector.infrastructure.client.casdoor.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.core.io.Resource

@ConfigurationProperties(prefix = "casdoor")
data class CasdoorProperties(
    val endpoint: String,
    val clientId: String,
    val clientSecret: String,
    val certificate: Resource,
    val organizationName: String,
    val applicationName: String,
)