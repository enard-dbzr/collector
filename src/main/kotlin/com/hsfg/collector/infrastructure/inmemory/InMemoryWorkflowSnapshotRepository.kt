package com.hsfg.collector.infrastructure.inmemory

import com.hsfg.collector.core.workflow.application.SnapshotRepositoryPort
import com.hsfg.collector.core.workflow.application.WorkflowSnapshot
import org.springframework.stereotype.Repository

@Repository
class InMemoryWorkflowSnapshotRepository : SnapshotRepositoryPort {

    private val snapshots: MutableMap<String, WorkflowSnapshot> = mutableMapOf()

    override fun save(
        sessionId: String,
        snapshot: WorkflowSnapshot
    ) {
        snapshots[sessionId] = snapshot
    }

    override fun load(sessionId: String): WorkflowSnapshot? {
        return snapshots[sessionId]
    }
}