package com.hsfg.collector.core.mediator.frame

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.frame.utils.AuthFrame
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.*
import org.springframework.stereotype.Component
import java.util.*


class RootFrame(
    private var currentState: Frame<BotContext, BotEvent, *>? = null,
    private var interceptorFrame: AuthFrame? = null,
    private val interceptorFrameFactory: AuthFrame.AuthFrameFactory,
) : Frame<BotContext, BotEvent, Nothing?> {

    override fun onEnter(context: BotContext): FrameResult<Nothing?> {
        return FrameResult.Continue(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<Nothing?> {
        if (interceptAttempt(context, event)) return FrameResult.Continue(null)

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

    private fun interceptAttempt(context: BotContext, event: BotEvent): Boolean {
        if (interceptorFrame != null) {
            val handleResult = interceptorFrame!!.handle(context, event)

            if (handleResult is FrameResult.Finished) {
                interceptorFrame!!.onExit(context)
                interceptorFrame = null
            }

            return true
        }

        interceptorFrame = interceptorFrameFactory.createIfNeeded(context, event)
        val frameResult = interceptorFrame?.onEnter(context)
        if (frameResult is FrameResult.Finished) {
            interceptorFrame!!.onExit(context)
            interceptorFrame = null
        }

        return interceptorFrame != null
    }


    @Component
    class RootFrameFactory(
        private val authFrameFactory: AuthFrame.AuthFrameFactory,
    ) : DataFactory<RootFrame> {

        override fun serialize(objectPool: ObjectPool, instance: RootFrame): JsonElement {
            return buildJsonObject {
                put("currentState", instance.currentState?.let { objectPool.put(it) }?.value?.toString())
                put("interceptorFrame", instance.interceptorFrame?.let { objectPool.put(it) }?.value?.toString())
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun create(objectPool: ObjectPool, data: JsonElement): RootFrame {
            val currentState = data.jsonObject["currentState"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), Frame::class)
            } as Frame<BotContext, BotEvent, *>?

            val interceptorFrame = data.jsonObject["interceptorFrame"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), AuthFrame::class)
            }

            return RootFrame(currentState, interceptorFrame, authFrameFactory)
        }

        fun createNew() = RootFrame(interceptorFrameFactory = authFrameFactory)
    }
}
