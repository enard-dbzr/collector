package com.hsfg.collector.core.workflow.application

import com.hsfg.collector.core.workflow.domain.objectpool.DataSnapshot
import kotlinx.serialization.json.JsonElement
import java.util.UUID

data class WorkflowSnapshot(val root: JsonElement, val pool: Map<UUID, DataSnapshot>)
