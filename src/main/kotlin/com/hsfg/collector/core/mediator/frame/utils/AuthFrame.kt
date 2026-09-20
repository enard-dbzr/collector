package com.hsfg.collector.core.mediator.frame.utils

import com.hsfg.collector.core.interaction.application.dto.outgoing.EditMessageBody
import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageAttachment
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.user.application.serivce.ChatAuthenticationService
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.*
import org.springframework.stereotype.Component
import java.util.*

class AuthFrame private constructor(
    private val chatAuthenticationService: ChatAuthenticationService,
    private var sentMessageId: MessageId?,
) : Frame<BotContext, BotEvent, Nothing?> {

    constructor(chatAuthenticationService: ChatAuthenticationService) : this(chatAuthenticationService, null)

    override fun onEnter(context: BotContext): FrameResult<Nothing?> {
        val link = chatAuthenticationService.createLoginUrl(context.chatId)

        sentMessageId = context.deliveryService.sendMessage(context.chatId, MessageBody(
            "Для продолжения необходимо авторизоваться",
            listOf(
                MessageAttachment.ButtonAttachment("Войти", inlineUrl = link)
            )
        )
        )
        return FrameResult.Continue(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<Nothing?> {
        if (event !is BotEvent.Authorized)
            return FrameResult.Continue(null)

        val sentMessageId = sentMessageId ?: error("Sent message id is null")
        context.deliveryService.editMessage(
            sentMessageId,
            EditMessageBody(text = "Вы успешно авторизовались", attachments = listOf())
        )

        return FrameResult.Finished(null)
    }

    @Component
    class AuthFrameFactory(
        private val chatAuthenticationService: ChatAuthenticationService
    ) : DataFactory<AuthFrame> {
        override fun serialize(objectPool: ObjectPool, instance: AuthFrame): JsonElement {
            return buildJsonObject {
                put("sentMessageId", instance.sentMessageId?.let { objectPool.put(it) }?.value?.toString())
            }
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): AuthFrame {
            val sentMessageId = data.jsonObject["sentMessageId"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), MessageId::class)
            }

            return AuthFrame(chatAuthenticationService, sentMessageId)
        }

        fun createIfNeeded(context: BotContext, event: BotEvent) : AuthFrame? {
            if (!chatAuthenticationService.isAuthenticated(context.chatId) &&
                event is BotEvent.ChatHandled) {
                return AuthFrame(chatAuthenticationService)
            }
            return null
        }

        fun createNew() = AuthFrame(chatAuthenticationService)
    }
}