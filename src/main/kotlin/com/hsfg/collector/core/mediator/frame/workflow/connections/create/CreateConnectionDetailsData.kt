package com.hsfg.collector.core.mediator.frame.workflow.connections.create

import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.*
import java.util.*

data class CreateConnectionDetailsData(
    var userId: UserId? = null,
    var name: String = "",
    var details: String = "",
    var notes: String = "",
) {

    class Factory : DataFactory<CreateConnectionDetailsData> {
        override fun serialize(objectPool: ObjectPool, instance: CreateConnectionDetailsData) = buildJsonObject {
            put("user_id", instance.userId?.let { objectPool.put(it).value.toString() })
            put("name", instance.name)
            put("details", instance.details)
            put("notes", instance.notes)
        }


        override fun create(objectPool: ObjectPool, data: JsonElement): CreateConnectionDetailsData {
            val userId = data.jsonObject["user_id"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), UserId::class)
            }

            return CreateConnectionDetailsData(
                userId = userId,
                name = data.jsonObject["name"]?.jsonPrimitive?.contentOrNull ?: "",
                details = data.jsonObject["details"]?.jsonPrimitive?.contentOrNull ?: "",
                notes = data.jsonObject["notes"]?.jsonPrimitive?.contentOrNull ?: ""
            )
        }

    }
}
