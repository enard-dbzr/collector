package com.hsfg.collector.core.mediator.frame.utils.ask

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.interaction.application.dto.outgoing.EditMessageBody
import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageAttachment
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.*
import java.util.*

class AskTextFrame(
    private val message: String,
    private val skippable: Boolean = false,
    private val messageAfterSkip: String? = null
) : Frame<BotContext, BotEvent, String?> {

    private var sentMessageId: MessageId? = null

    override fun onEnter(context: BotContext): FrameResult<String?> {
        sentMessageId = context.deliveryService.sendMessage(
            context.chatId,
            MessageBody(
                message,
                attachments = if (skippable) listOf(
                    MessageAttachment.ButtonAttachment("Пропустить", tag = "skip")
                ) else listOf()
            )
        )
        return FrameResult.Continue(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<String?> {
        if (
            event is BotEvent.ChatHandled &&
            event.event is IncomingEvent.ButtonClicked &&
            event.event.source == sentMessageId
        ) {
            context.deliveryService.editMessage(
                sentMessageId ?: error("Sent message id is null"),
                EditMessageBody(
                    text = messageAfterSkip ?: "Вы пропустили ввод текста",
                    attachments = emptyList()
                )
            )

            return FrameResult.Finished("")
        }

        if (event is BotEvent.ChatHandled && event.event is IncomingEvent.MessageReceived) {
            return FrameResult.Finished(event.event.body.text)
        }

        return FrameResult.Continue(null)
    }

    override fun onExit(context: BotContext) {
        val sentMessageId = sentMessageId ?: return

        context.deliveryService.editMessage(
            sentMessageId,
            EditMessageBody(attachments = emptyList())
        )
    }

    class Factory : DataFactory<AskTextFrame> {
        override fun serialize(objectPool: ObjectPool, instance: AskTextFrame): JsonElement {
            return buildJsonObject {
                put("message", instance.message)
                put("skippable", instance.skippable)
                put("messageAfterSkip", instance.messageAfterSkip)

                put("sentMessageId", instance.sentMessageId?.let { objectPool.put(it) }?.value?.toString())
            }
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): AskTextFrame {
            val message = data.jsonObject["message"]?.jsonPrimitive?.contentOrNull
            val skippable = data.jsonObject["skippable"]?.jsonPrimitive?.booleanOrNull
            val messageAfterSkip = data.jsonObject["messageAfterSkip"]?.jsonPrimitive?.contentOrNull

            val instance = AskTextFrame(
                message ?: error("Message is null"),
                skippable ?: false,
                messageAfterSkip
            )

            instance.sentMessageId = data.jsonObject["sentMessageId"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), MessageId::class)
            }

            return instance
        }
    }
}