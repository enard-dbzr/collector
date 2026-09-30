package com.hsfg.collector.infrastructure.client.casdoor.config

import org.casbin.casdoor.config.Config
import org.casbin.casdoor.service.AuthService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class CasdoorClientConfiguration(
    private val properties: CasdoorProperties
) {

    @Bean
    fun getAuthService(): AuthService {
        return AuthService(properties.toConfig())
    }
}

private fun CasdoorProperties.toConfig(): Config {
    return Config(
        this.endpoint,
        this.clientId,
        this.clientSecret,
        this.certificate.file.readText(),
        this.organizationName,
        this.applicationName
    )
}