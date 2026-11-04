package com.hsfg.collector.core.mediator.frame.workflow

import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.frame.utils.message.SendMessageFrame
import com.hsfg.collector.core.mediator.textbuilder.HelpTextBuilder
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import org.springframework.stereotype.Component

class HelpWorkflow(
    private val helpTextBuilder: HelpTextBuilder,
) : Frame<BotContext, BotEvent, Nothing?> {

    override fun onEnter(context: BotContext): FrameResult<Nothing?> {
        SendMessageFrame(helpTextBuilder.buildText()).onEnter(context)
        return FrameResult.Finished(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<Nothing?> {
        return FrameResult.Finished(null)
    }


    @Component
    class Factory(
        private val helpTextBuilder: HelpTextBuilder,
    ) : DataFactory<HelpWorkflow> {
        override fun serialize(objectPool: ObjectPool, instance: HelpWorkflow): JsonElement {
            return buildJsonObject {}
        }

        @Suppress("UNCHECKED_CAST")
        override fun create(objectPool: ObjectPool, data: JsonElement): HelpWorkflow {
            return createNew()
        }

        fun createNew() = HelpWorkflow(helpTextBuilder)
    }
}