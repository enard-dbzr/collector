package com.hsfg.collector.core.workflow.domain.frame

sealed interface FrameResult<out R> {

    val value: R

    data class Continue<R>(override val value: R) : FrameResult<R>

    data class Finished<R>(override val value: R) : FrameResult<R>
}