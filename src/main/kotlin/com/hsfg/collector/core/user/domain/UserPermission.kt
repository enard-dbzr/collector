package com.hsfg.collector.core.user.domain

enum class UserPermission(val resource: String, val action: String) {
    WRITE_CONNECTIONS("connections", "Write"),
    READ_OWN_CONNECTIONS("connections:own", "Read"),

    WRITE_INVITATIONS("invitations", "Write"),
}