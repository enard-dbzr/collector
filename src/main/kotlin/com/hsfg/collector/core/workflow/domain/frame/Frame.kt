package com.hsfg.collector.core.workflow.domain.frame

interface Frame<in C : FrameContext, in E : FrameEvent, out R> {

    fun onEnter(context: C): FrameResult<R>

    fun handle(context: C, event: E): FrameResult<R>

    fun onExit(context: C) {}
}