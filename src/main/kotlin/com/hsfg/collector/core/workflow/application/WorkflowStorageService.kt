package com.hsfg.collector.core.workflow.application

import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactoryRegistry
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class WorkflowStorageService(
    private val factoryRegistry: DataFactoryRegistry,
    private val snapshotRepository: SnapshotRepositoryPort
) {

    fun store(sessionId: String, root: Frame<*, *, *>) {
        val pool = ObjectPool(factoryRegistry)
        val factory = factoryRegistry.findByType(root::class).factory

        val rootSnapshot = factory.serialize(pool, root)
        val poolSnapshot = pool.dump()

        snapshotRepository.save(
            sessionId,
            WorkflowSnapshot(rootSnapshot, poolSnapshot)
        )
    }

    fun <T : Frame<*, *, *>> load(sessionId: String, rootType: KClass<T>): T? {
        val pool = ObjectPool(factoryRegistry)

        val factory = factoryRegistry.findByType(rootType).factory

        val snapshot = snapshotRepository.load(sessionId) ?: return null

        pool.load(snapshot.pool)

        return factory.create(pool, snapshot.root)
    }
}