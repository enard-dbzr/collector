package com.hsfg.collector.core.mediator.datafactory

import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

class MessageIdFactory : DataFactory<MessageId> {

    override fun serialize(
        objectPool: ObjectPool,
        instance: MessageId
    ): JsonElement {
        return JsonPrimitive(instance.value)
    }

    override fun create(
        objectPool: ObjectPool,
        data: JsonElement
    ): MessageId {
        return MessageId(data.jsonPrimitive.content)
    }
}