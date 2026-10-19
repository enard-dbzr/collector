package com.hsfg.collector.core.mediator.datafactory

import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

class UserIdFactory : DataFactory<UserId> {

    override fun serialize(
        objectPool: ObjectPool,
        instance: UserId
    ): JsonElement {
        return JsonPrimitive(instance.value)
    }

    override fun create(
        objectPool: ObjectPool,
        data: JsonElement
    ): UserId {
        return UserId(data.jsonPrimitive.content)
    }
}