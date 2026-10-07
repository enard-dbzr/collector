package com.hsfg.collector.core.connection.application.exception

import com.hsfg.collector.core.user.domain.UserId

class ConnectionDetailsNotFound(userId: UserId, connectionId: String) :
    Exception("Connection details with id '$connectionId' not found for user with ID '${userId.value}'.")
