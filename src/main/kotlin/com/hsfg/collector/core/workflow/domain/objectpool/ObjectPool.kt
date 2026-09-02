package com.hsfg.collector.core.workflow.domain.objectpool

import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.cast

class ObjectPool(
    private val factoryRegistry: DataFactoryRegistry
) {

    private val toStore = IdentityHashMap<Any, UUID>()
    private val toLoad = HashMap<UUID, Any>()

    private val serialized = HashMap<UUID, DataSnapshot>()

    @OptIn(ExperimentalContracts::class)
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> put(reference: T): PoolId {
        val existingUuid = toStore[reference]
        if (existingUuid != null) {
            return PoolId(existingUuid)
        }

        val factoryWithId = factoryRegistry.findByType(reference::class)

        var objectUuid: UUID = UUID.randomUUID()
        while (serialized.containsKey(objectUuid)) {
            objectUuid = UUID.randomUUID()
        }

        toStore[reference] = objectUuid

        serialized[objectUuid] = DataSnapshot(
            factoryWithId.id,
            factoryWithId.factory.serialize(this, reference)
        )

        return PoolId(objectUuid)
    }

    @OptIn(ExperimentalContracts::class)
    fun <T : Any> getData(poolId: PoolId?, type: KClass<T>): T? {
        contract {
            returns(null) implies (poolId == null)
            returnsNotNull() implies (poolId != null)
        }

        if (poolId == null) {
            return null
        }

        if (toLoad.containsKey(poolId.value)) {
            return type.cast(toLoad[poolId.value])
        }

        val dataSnapshot = serialized[poolId.value] ?: error("No data found for poolId: ${poolId.value}")

        val factory = factoryRegistry.get(dataSnapshot.serializerId, type)

        val instance = factory.factory.create(this, dataSnapshot.data)

        toLoad[poolId.value] = instance

        return instance
    }

    inline fun <reified T : Any> getData(poolId: PoolId?): T? {
        return getData(poolId, T::class)
    }

    fun dump(): Map<UUID, DataSnapshot> {
        val usedIds = toStore.values.toSet()

        val dataDump = serialized.filterKeys { usedIds.contains(it) }

        return dataDump
    }

    fun load(dataDump: Map<UUID, DataSnapshot>) {
        serialized.clear()
        toStore.clear()
        toLoad.clear()

        serialized.putAll(dataDump)
    }
}
