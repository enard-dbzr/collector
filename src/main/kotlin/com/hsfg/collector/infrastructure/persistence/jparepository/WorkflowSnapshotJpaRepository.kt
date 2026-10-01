package com.hsfg.collector.infrastructure.persistence.jparepository

import com.hsfg.collector.infrastructure.persistence.entity.WorkflowSnapshotEntity
import org.springframework.data.jpa.repository.JpaRepository

interface WorkflowSnapshotJpaRepository : JpaRepository<WorkflowSnapshotEntity, String>