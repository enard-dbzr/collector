package com.hsfg.collector.core.mediator.frame.workflow.connections.create

import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.*

data class CreateConnectionDetailsData(
    var name: String = "",
    var details: String = "",
    var notes: String = "",
) {

    class Factory : DataFactory<CreateConnectionDetailsData> {
        override fun serialize(objectPool: ObjectPool, instance: CreateConnectionDetailsData) = buildJsonObject {
            put("name", instance.name)
            put("details", instance.details)
            put("notes", instance.notes)
        }


        override fun create(objectPool: ObjectPool, data: JsonElement) = CreateConnectionDetailsData(
            name = data.jsonObject["name"]?.jsonPrimitive?.contentOrNull ?: "",
            details = data.jsonObject["details"]?.jsonPrimitive?.contentOrNull ?: "",
            notes = data.jsonObject["notes"]?.jsonPrimitive?.contentOrNull ?: ""
        )

    }
}
