package com.hsfg.collector.core.workflow.domain.objectpool

import kotlinx.serialization.json.JsonElement

interface DataFactory<R> {

    fun serialize(objectPool: ObjectPool, instance: R): JsonElement

    fun create(objectPool: ObjectPool, data: JsonElement): R
}