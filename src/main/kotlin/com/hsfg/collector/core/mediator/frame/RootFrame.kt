package com.hsfg.collector.core.mediator.frame

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.*
import java.util.*


class RootFrame(
    private var currentState: Frame<BotContext, BotEvent, *>? = null
) : Frame<BotContext, BotEvent, Nothing?> {

    override fun onEnter(context: BotContext): FrameResult<Nothing?> {
        return FrameResult.Continue(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<Nothing?> {
        if (currentState != null) {
            val handleResult = currentState!!.handle(context, event)

            if (handleResult is FrameResult.Finished) {
                currentState!!.onExit(context)
                currentState = null
            }

            return FrameResult.Continue(null)
        }

        if (event is BotEvent.ChatHandled && event.event is IncomingEvent.MessageReceived) {
            when (event.event.body.text) {
                "/start" -> {
                    changeState(context, StartWorkflow())
                }
            }
        }

        return FrameResult.Continue(null)
    }

    private fun changeState(context: BotContext, nextFrame: Frame<BotContext, BotEvent, *>?) {
        currentState?.onExit(context)

        currentState = nextFrame

        val enterResult = currentState?.onEnter(context)

        if (enterResult is FrameResult.Finished) {
            currentState!!.onExit(context)
            currentState = null
        }
    }

    class RootFrameFactory : DataFactory<RootFrame> {
        override fun serialize(objectPool: ObjectPool, instance: RootFrame): JsonElement {
            return buildJsonObject {
                put("currentState", instance.currentState?.let { objectPool.put(it) }?.value?.toString())
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun create(objectPool: ObjectPool, data: JsonElement): RootFrame {
            if (data.jsonObject["currentState"] is JsonNull) {
                return RootFrame()
            }

            val currentStateId = UUID.fromString(data.jsonObject["currentState"]!!.jsonPrimitive.content)
            val currentState = objectPool.getData(PoolId(currentStateId), Frame::class)
                    as Frame<BotContext, BotEvent, Any>?

            return RootFrame(currentState)
        }
    }
}
