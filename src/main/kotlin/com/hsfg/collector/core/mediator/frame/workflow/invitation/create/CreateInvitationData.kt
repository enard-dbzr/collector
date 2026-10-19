package com.hsfg.collector.core.mediator.frame.workflow.invitation.create

import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.*
import java.util.*

data class CreateInvitationData(
    var inviter: UserId? = null,
    var notes: String = "",
) {

    class Factory : DataFactory<CreateInvitationData> {
        override fun serialize(objectPool: ObjectPool, instance: CreateInvitationData) = buildJsonObject {
            put("inviter", instance.inviter?.let { objectPool.put(it).value.toString() })
            put("notes", instance.notes)
        }


        override fun create(objectPool: ObjectPool, data: JsonElement): CreateInvitationData {
            val inviter = data.jsonObject["inviter"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), UserId::class)
            }

            val notes = data.jsonObject["notes"]?.jsonPrimitive?.contentOrNull ?: ""

            return CreateInvitationData(
                inviter = inviter,
                notes = notes
            )
        }

    }
}
