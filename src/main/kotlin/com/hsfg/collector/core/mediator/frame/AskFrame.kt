package com.hsfg.collector.core.mediator.frame

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.*

class AskFrame(
    private val message: String
) : Frame<BotContext, BotEvent, String?> {

    override fun onEnter(context: BotContext): FrameResult<String?> {
        context.deliveryService.sendMessage(context.chatId, MessageBody(message))
        return FrameResult.Continue(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<String?> {
        if (event is BotEvent.ChatHandled && event.event is IncomingEvent.MessageReceived) {
            return FrameResult.Finished(event.event.body.text)
        }

        return FrameResult.Continue(null)
    }

    class AskFrameFactory : DataFactory<AskFrame> {
        override fun serialize(objectPool: ObjectPool, instance: AskFrame): JsonElement {
            return buildJsonObject {
                put("message", instance.message)
            }
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): AskFrame {
            val message = data.jsonObject["message"]!!.jsonPrimitive.content

            return AskFrame(message)
        }
    }
}