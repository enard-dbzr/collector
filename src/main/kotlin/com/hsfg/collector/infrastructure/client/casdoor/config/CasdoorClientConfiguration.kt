package com.hsfg.collector.infrastructure.client.casdoor.config

import com.hsfg.collector.infrastructure.client.casdoor.custom.CasdoorUserService
import org.casbin.casdoor.config.Config
import org.casbin.casdoor.service.AuthService
import org.casbin.casdoor.service.EnforcerService
import org.casbin.casdoor.service.UserService
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

    @Bean
    fun getEnforcerService(): EnforcerService {
        return EnforcerService(properties.toConfig())
    }

    @Bean
    fun getUserService(): UserService {
        return UserService(properties.toConfig())
    }

    @Bean
    fun getCasdoorUserService(): CasdoorUserService {
        return CasdoorUserService(properties.toConfig())
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