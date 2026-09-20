package com.hsfg.collector.core.mediator.frame.workflow

import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.frame.SendMessageFrame
import com.hsfg.collector.core.mediator.frame.utils.AuthFrame
import com.hsfg.collector.core.workflow.application.frame.sequence.FrameKey
import com.hsfg.collector.core.workflow.application.frame.sequence.FrameStep
import com.hsfg.collector.core.workflow.application.frame.sequence.SequenceFrame
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.springframework.stereotype.Component
import java.util.UUID

class StartWorkflow private constructor(
    private val sequence: SequenceFrame<BotContext, BotEvent>,
) : Frame<BotContext, BotEvent, Any?> {

    constructor(authFrameFactory: AuthFrame.AuthFrameFactory) : this(
        SequenceFrame(
            listOf(
                FrameStep(FrameKey(), authFrameFactory.createNew()),
                FrameStep(FrameKey(), SendMessageFrame("Hello"))
            )
        ),
    )

    override fun onEnter(context: BotContext): FrameResult<*> {
        return sequence.onEnter(context)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<*> {
        return sequence.handle(context, event)
    }

    override fun onExit(context: BotContext) {
        sequence.onExit(context)
    }

    @Component
    class StartWorkflowFactory(
        private val authFrameFactory: AuthFrame.AuthFrameFactory,
    ) : DataFactory<StartWorkflow> {
        override fun serialize(objectPool: ObjectPool, instance: StartWorkflow): JsonElement {
            return buildJsonObject {
                put("sequence", objectPool.put(instance.sequence).value.toString())
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun create(objectPool: ObjectPool, data: JsonElement): StartWorkflow {
            val sequenceId = UUID.fromString(data.jsonObject["sequence"]!!.jsonPrimitive.content)
            val sequence = objectPool.getData(PoolId(sequenceId), SequenceFrame::class)
                    as SequenceFrame<BotContext, BotEvent>

            return StartWorkflow(sequence)
        }

        fun createNew() = StartWorkflow(authFrameFactory)
    }
}