package com.hsfg.collector.core.workflow.application

import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactoryRegistry
import org.springframework.stereotype.Repository
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@Repository
class DefaultDataFactoryRegistry : DataFactoryRegistry {

    private val typeToId = HashMap<KClass<*>, String>()
    private val idToType = HashMap<String, KClass<*>>()
    private val idToFactory = HashMap<String, DataFactory<*>>()

    fun <T : Any> register(id: String, type: KClass<T>, factory: DataFactory<T>) {
        typeToId[type] = id
        idToType[id] = type
        idToFactory[id] = factory
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> findByType(type: KClass<out T>): DataFactoryRegistry.FactoryWithId<T> {
        val id = typeToId[type] ?: error("No factory registered for type: ${type.simpleName}")
        val factory = idToFactory[id] as DataFactory<T>

        return DataFactoryRegistry.FactoryWithId(factory, id)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(id: String, type: KClass<out T>): DataFactoryRegistry.FactoryWithId<T> {
        val registeredType = idToType[id] ?: error("No factory registered for id: $id")

        require(registeredType.isSubclassOf(type)) { "Type mismatch: expected $type but got $registeredType" }

        val factory = idToFactory[id] as DataFactory<T>

        return DataFactoryRegistry.FactoryWithId(factory, id)
    }

}
