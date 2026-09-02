package com.hsfg.collector.core.workflow.application

interface SnapshotRepositoryPort {
    fun save(sessionId: String, snapshot: WorkflowSnapshot)
    fun load(sessionId: String): WorkflowSnapshot?
}