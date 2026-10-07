package com.hsfg.collector.core.user.application.serivce

import com.hsfg.collector.core.user.domain.UserPermission
import com.hsfg.collector.infrastructure.client.casdoor.config.CasdoorProperties
import org.casbin.casdoor.service.EnforcerService
import org.springframework.stereotype.Service

@Service
class PermissionsService(
    private val enforcerService: EnforcerService,
    private val casdoorProperties: CasdoorProperties
) {

    fun check(subject: String, permission: UserPermission): Boolean {
        return enforcerService.enforce(
            null,
            null,
            null,
            null,
            casdoorProperties.organizationName,
            arrayOf(subject, permission.resource, permission.action),
        )
    }
}