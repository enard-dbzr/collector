package com.hsfg.collector.infrastructure.persistence

import com.hsfg.collector.core.workflow.application.SnapshotRepositoryPort
import com.hsfg.collector.core.workflow.application.WorkflowSnapshot
import com.hsfg.collector.core.workflow.domain.objectpool.DataSnapshot
import com.hsfg.collector.infrastructure.persistence.entity.WorkflowSnapshotEntity
import com.hsfg.collector.infrastructure.persistence.jparepository.WorkflowSnapshotJpaRepository
import kotlinx.serialization.json.*
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class DbWorkflowSnapshotRepository(
    private val jpaRepository: WorkflowSnapshotJpaRepository,
) : SnapshotRepositoryPort {

    override fun save(
        sessionId: String,
        snapshot: WorkflowSnapshot
    ) {
        jpaRepository.save(
            WorkflowSnapshotEntity(
                sessionId = sessionId,
                snapshot = snapshot.toJson().toString(),
            )
        )
    }

    override fun load(sessionId: String): WorkflowSnapshot? {
        return jpaRepository.findById(sessionId)
            .map { Json.parseToJsonElement(it.snapshot).toWorkflowSnapshot() }
            .orElse(null)
    }
}

private fun WorkflowSnapshot.toJson() =
    buildJsonObject {
        put("root", root)

        putJsonObject("pool") {
            pool.forEach { (id, snapshot) ->
                putJsonObject(id.toString()) {
                    put("serializerId", snapshot.serializerId)
                    put("data", snapshot.data)
                }
            }
        }
    }

private fun JsonElement.toWorkflowSnapshot(): WorkflowSnapshot {
    val pool = jsonObject["pool"]!!
        .jsonObject
        .mapKeys { (id, _) -> UUID.fromString(id) }
        .mapValues { (_, value) ->
            val snapshot = value.jsonObject

            DataSnapshot(
                serializerId = snapshot["serializerId"]!!.jsonPrimitive.content,
                data = snapshot["data"]!!
            )
        }

    return WorkflowSnapshot(
        root = jsonObject["root"]!!,
        pool = pool
    )
}