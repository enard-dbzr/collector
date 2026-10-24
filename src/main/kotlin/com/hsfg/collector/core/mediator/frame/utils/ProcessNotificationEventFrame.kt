package com.hsfg.collector.core.mediator.frame.utils

import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.mapper.NotificationTextMapper
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import org.springframework.stereotype.Component

class ProcessNotificationEventFrame(
    private val notificationTextMapper: NotificationTextMapper,
) : Frame<BotContext, BotEvent, Nothing?> {

    override fun onEnter(context: BotContext): FrameResult<Nothing?> {
        return FrameResult.Continue(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<Nothing?> {
        if (event !is BotEvent.NotificationEvent)
            return FrameResult.Finished(null)

        context.deliveryService.sendMessage(
            context.chatId,
            MessageBody(text = notificationTextMapper.buildText(event.notification))
        )

        return FrameResult.Finished(null)
    }

    @Component
    class Factory(
        private val notificationTextMapper: NotificationTextMapper,
    ) : DataFactory<ProcessNotificationEventFrame> {

        override fun serialize(objectPool: ObjectPool, instance: ProcessNotificationEventFrame): JsonElement {
            return buildJsonObject { }
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): ProcessNotificationEventFrame {
            return createNew()
        }

        fun createNew() = ProcessNotificationEventFrame(notificationTextMapper)
    }
}