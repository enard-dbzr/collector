package com.hsfg.collector.core.mediator.frame

import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.*

class SendMessageFrame(
    private val message: String
) : Frame<BotContext, BotEvent, MessageId?> {

    override fun onEnter(context: BotContext): FrameResult<MessageId> {
        val messageId = context.deliveryService.sendMessage(context.chatId, MessageBody(message))

        return FrameResult.Finished(messageId)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<MessageId?> {
        return FrameResult.Finished(null)
    }

    class SendMessageFrameFactory : DataFactory<SendMessageFrame> {
        override fun serialize(objectPool: ObjectPool, instance: SendMessageFrame): JsonElement {
            return buildJsonObject {
                put("message", instance.message)
            }
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): SendMessageFrame {
            val message = data.jsonObject["message"]!!.jsonPrimitive.content

            return SendMessageFrame(message)
        }
    }
}