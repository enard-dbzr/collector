package com.hsfg.collector.core.workflow.application.frame.sequence

import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameContext
import com.hsfg.collector.core.workflow.domain.frame.FrameEvent

data class FrameStep<in C : FrameContext, in E : FrameEvent, R>(
    val key: FrameKey<R>,
    val frame: Frame<C, E, R>,
)