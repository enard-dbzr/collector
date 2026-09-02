package com.hsfg.collector.core.workflow.domain.objectpool

import kotlinx.serialization.json.JsonElement

data class DataSnapshot(val serializerId: String, val data: JsonElement)
