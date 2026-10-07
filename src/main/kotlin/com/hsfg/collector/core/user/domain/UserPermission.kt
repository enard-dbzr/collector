package com.hsfg.collector.core.user.domain

enum class UserPermission(val resource: String, val action: String) {
    WRITE_CONNECTIONS("connections", "Write"),
}