package com.hsfg.collector.infrastructure.persistence

import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

internal fun DataIntegrityViolationException.isUniqueConstraintViolation(
    constraintName: String? = null,
): Boolean {
    var cause: Throwable? = this

    while (cause != null) {
        when (cause) {
            is ConstraintViolationException -> {
                if (
                    cause.kind == ConstraintViolationException.ConstraintKind.UNIQUE &&
                    (constraintName == null || cause.constraintName == constraintName)
                ) {
                    return true
                }
            }

        }

        cause = cause.cause
    }

    return false
}