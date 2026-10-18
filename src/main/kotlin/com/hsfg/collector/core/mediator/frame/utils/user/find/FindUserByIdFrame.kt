package com.hsfg.collector.core.mediator.frame.utils.user.find

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.interaction.application.dto.outgoing.EditMessageBody
import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageAttachment
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.user.application.serivce.ChatAuthenticationService
import com.hsfg.collector.core.user.application.serivce.UserProfileService
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.*
import org.springframework.stereotype.Component
import java.util.*

class FindUserByIdFrame(
    private val chatAuthenticationService: ChatAuthenticationService,
    private val userProfileService: UserProfileService,

    private val message: String,
) : Frame<BotContext, BotEvent, UserId?> {

    companion object {
        const val SELECT_ME_BUTTON_TAG = "select_me"

        const val SELECT_ME_BUTTON_TEXT = "Выбрать меня"
        const val USER_NOT_FOUND_TEXT = "Пользователь с таким id не найден. Попробуйте еще раз."
        const val USER_FOUND_TEXT = "Выбран пользователь: %s (%s)"
    }

    private var sentMessageId: MessageId? = null

    override fun onEnter(context: BotContext): FrameResult<UserId?> {
        sentMessageId = context.deliveryService.sendMessage(
            context.chatId,
            MessageBody(
                message,
                attachments = listOf(
                    MessageAttachment.ButtonAttachment(SELECT_ME_BUTTON_TEXT, tag = SELECT_ME_BUTTON_TAG)
                )
            )
        )
        return FrameResult.Continue(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<UserId?> {
        if (
            event is BotEvent.ChatHandled &&
            event.event is IncomingEvent.ButtonClicked &&
            event.event.source == sentMessageId &&
            event.event.tag == SELECT_ME_BUTTON_TAG
        ) {
            val localUserProfile = chatAuthenticationService.getLocalUser(context.chatId)

            val selectedUserId = findUser(context, localUserProfile.id)
                ?: error("User profile not found for chat ${context.chatId}")

            return FrameResult.Finished(selectedUserId)
        }

        if (event is BotEvent.ChatHandled && event.event is IncomingEvent.MessageReceived) {
            val selectedUserId = findUser(context, UserId(event.event.body.text))
                ?: return FrameResult.Continue(null)

            return FrameResult.Finished(selectedUserId)
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

    private fun findUser(context: BotContext, userId: UserId): UserId? {
        val userProfile = userProfileService.getProfile(userId)

        if (userProfile == null) {
            context.deliveryService.sendMessage(
                context.chatId,
                MessageBody(USER_NOT_FOUND_TEXT)
            )
            return null
        }

        context.deliveryService.sendMessage(
            context.chatId,
            MessageBody(USER_FOUND_TEXT.format(userProfile.displayName, userProfile.email))
        )

        return userProfile.id
    }

    @Component
    class Factory(
        private val chatAuthenticationService: ChatAuthenticationService,
        private val userProfileService: UserProfileService,
    ) : DataFactory<FindUserByIdFrame> {

        companion object {
            const val DEFAULT_REQUEST_TEXT = "Введите id пользователя"
        }

        override fun serialize(objectPool: ObjectPool, instance: FindUserByIdFrame) = buildJsonObject {
            put("requestText", instance.message)

            put("sentMessageId", instance.sentMessageId?.let { objectPool.put(it) }?.value?.toString())
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): FindUserByIdFrame {
            val message = data.jsonObject["requestText"]?.jsonPrimitive?.contentOrNull
                ?: DEFAULT_REQUEST_TEXT

            val instance = FindUserByIdFrame(
                chatAuthenticationService,
                userProfileService,

                message
            )

            instance.sentMessageId = data.jsonObject["sentMessageId"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), MessageId::class)
            }

            return instance
        }

        fun createNew(message: String) = FindUserByIdFrame(
            chatAuthenticationService,
            userProfileService,

            message,
        )
    }
}