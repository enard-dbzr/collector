package com.hsfg.collector.core.mediator.frame.workflow.profile.read

import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.frame.utils.message.SendMessageFrame
import com.hsfg.collector.core.mediator.textbuilder.UserProfileTextBuilder
import com.hsfg.collector.core.user.application.serivce.ChatPermissionsService
import com.hsfg.collector.core.user.application.serivce.UserProfileService
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.user.domain.UserPermission
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.logging.log4j.CloseableThreadContext.put
import org.springframework.stereotype.Component

class ReadProfileWorkflow(
    private val userProfileTextBuilder: UserProfileTextBuilder,
    private val userProfileService: UserProfileService,
    private val chatPermissionsService: ChatPermissionsService,

    private val userId: String?,
) : Frame<BotContext, BotEvent, Nothing?> {

    companion object {
        const val HEADER_TEXT = "Профиль пользователя"
        const val USER_NOT_FOUND_MESSAGE_TEXT = "Пользователь не найден епта."
        const val SPECIFY_USER_MESSAGE_TEXT = "Укажите пользователя епта."
        const val FORBIDDEN_MESSAGE_TEXT = "У вас недостаточно прав епта. Пожалуйста, обратитесь к администратору."
    }

    override fun onEnter(context: BotContext): FrameResult<Nothing?> {
        if (!chatPermissionsService.check(context.chatId, UserPermission.READ_PROFILE)) {
            context.deliveryService.sendMessage(
                context.chatId,
                MessageBody(FORBIDDEN_MESSAGE_TEXT)
            )

            return FrameResult.Finished(null)
        }

        if (userId == null) {
            context.deliveryService.sendMessage(
                context.chatId,
                MessageBody(SPECIFY_USER_MESSAGE_TEXT)
            )
            return FrameResult.Finished(null)
        }

        val userId = UserId(userId)

        if (userProfileService.getProfile(userId) == null) {
            context.deliveryService.sendMessage(
                context.chatId,
                MessageBody(USER_NOT_FOUND_MESSAGE_TEXT)
            )
            return FrameResult.Finished(null)
        }

        SendMessageFrame(
            userProfileTextBuilder.buildProfile(
                userId,
                HEADER_TEXT
            )
        ).onEnter(context)

        return FrameResult.Finished(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<Nothing?> {
        return FrameResult.Finished(null)
    }


    @Component
    class Factory(
        private val userProfileTextBuilder: UserProfileTextBuilder,
        private val userProfileService: UserProfileService,
        private val chatPermissionsService: ChatPermissionsService,
    ) : DataFactory<ReadProfileWorkflow> {

        override fun serialize(objectPool: ObjectPool, instance: ReadProfileWorkflow): JsonElement {
            return buildJsonObject {
                put("userId", instance.userId)
            }
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): ReadProfileWorkflow {
            return createNew(
                data.jsonObject["userId"]?.jsonPrimitive?.contentOrNull
            )
        }

        fun createNew(userId: String?) = ReadProfileWorkflow(
            userProfileTextBuilder,
            userProfileService,
            chatPermissionsService,

            userId
        )
    }
}