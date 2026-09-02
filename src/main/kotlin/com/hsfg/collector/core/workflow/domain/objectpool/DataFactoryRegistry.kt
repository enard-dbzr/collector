package com.hsfg.collector.core.workflow.domain.objectpool

import kotlin.reflect.KClass


interface DataFactoryRegistry {

    fun <T : Any> findByType(type: KClass<out T>): FactoryWithId<T>

    fun <T : Any> get(id: String, type: KClass<out T>): FactoryWithId<T>

    data class FactoryWithId<T>(val factory: DataFactory<T>, val id: String)
}