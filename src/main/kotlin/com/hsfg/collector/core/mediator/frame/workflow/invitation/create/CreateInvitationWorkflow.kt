package com.hsfg.collector.core.mediator.frame.workflow.invitation.create

import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.invitation.application.dto.CreateInvitationCommand
import com.hsfg.collector.core.invitation.application.service.InvitationService
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.frame.utils.ask.AskTextFrame
import com.hsfg.collector.core.mediator.frame.utils.user.find.FindUserByIdFrame
import com.hsfg.collector.core.user.application.serivce.ChatPermissionsService
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.user.domain.UserPermission
import com.hsfg.collector.core.workflow.application.frame.sequence.FrameKey
import com.hsfg.collector.core.workflow.application.frame.sequence.FrameStep
import com.hsfg.collector.core.workflow.application.frame.sequence.SequenceFrame
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.*
import org.springframework.stereotype.Component
import java.util.*

class CreateInvitationWorkflow private constructor(
    private val chatPermissionsService: ChatPermissionsService,
    private val invitationService: InvitationService,

    private val data: CreateInvitationData,
    private val sequence: SequenceFrame<BotContext, BotEvent>,
) : Frame<BotContext, BotEvent, Any?> {

    companion object {
        val log = KotlinLogging.logger { }

        const val FIND_USER_REQUEST_TEXT = "Введите id приглашающего пользователя"
        const val NOTES_MESSAGE_TEXT = "Введите заметки для приглашения"
        const val NOTES_SKIPPED_MESSAGE_TEXT = "Вы пропустили ввод заметок"
        const val GENERATED_INVITATION_MESSAGE_TEXT = "<b>Приглашение создано</b>\n\n" +
                "Код приглашения: <code>%s</code>\n\n" +
                "Пригласите пользователя, передав ему этот код. Он понадобится при регистрации."

        val FIND_USER_KEY = FrameKey<UserId?>("find_user")
        val ASK_NOTES_KEY = FrameKey<String?>("notes")
    }

    constructor(
        chatPermissionsService: ChatPermissionsService,
        invitationService: InvitationService,
        findUserByIdFrameFactory: FindUserByIdFrame.Factory,
    ) : this(
        chatPermissionsService,
        invitationService,

        CreateInvitationData(),
        SequenceFrame(
            listOf(
                FrameStep(
                    FIND_USER_KEY,
                    findUserByIdFrameFactory.createNew(FIND_USER_REQUEST_TEXT)
                ),
                FrameStep(
                    ASK_NOTES_KEY,
                    AskTextFrame(
                        message = NOTES_MESSAGE_TEXT,
                        skippable = true,
                        messageAfterSkip = NOTES_SKIPPED_MESSAGE_TEXT
                    )
                )
            )
        ),
    )

    override fun onEnter(context: BotContext): FrameResult<*> {
        if (validatePermissions(context).not()) {
            return FrameResult.Finished(null)
        }

        return sequence.onEnter(context)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<*> {
        val result = sequence.handle(context, event)

        result.value.results(FIND_USER_KEY).mapNotNull { it.value }.forEach {
            data.inviter = it
        }

        result.value.results(ASK_NOTES_KEY).mapNotNull { it.value }.forEach {
            data.notes = it
        }

        if (result is FrameResult.Finished) {
            if (validatePermissions(context).not()) {
                return FrameResult.Finished(null)
            }

            val code = invitationService.create(
                CreateInvitationCommand(
                    inviter = data.inviter ?: error("inviter not set"),
                    notes = data.notes
                )
            )

            context.deliveryService.sendMessage(
                context.chatId,
                MessageBody(
                    text = GENERATED_INVITATION_MESSAGE_TEXT.format(code),
                )
            )

            log.info { "Create invitation workflow finished for chat ${context.chatId}" }
        }

        return result
    }

    override fun onExit(context: BotContext) {
        sequence.onExit(context)
    }

    private fun validatePermissions(context: BotContext): Boolean {
        if (chatPermissionsService.check(context.chatId, UserPermission.WRITE_INVITATIONS).not()) {
            context.deliveryService.sendMessage(
                context.chatId,
                MessageBody("У вас недостаточно прав. Пожалуйста, обратитесь к администратору.")
            )

            return false
        }

        return true
    }

    @Component
    class Factory(
        private val chatPermissionsService: ChatPermissionsService,
        private val invitationService: InvitationService,
        private val findUserByIdFrameFactory: FindUserByIdFrame.Factory,
    ) : DataFactory<CreateInvitationWorkflow> {

        override fun serialize(objectPool: ObjectPool, instance: CreateInvitationWorkflow): JsonElement {
            return buildJsonObject {
                put("data", objectPool.put(instance.data).value.toString())
                put("sequence", objectPool.put(instance.sequence).value.toString())
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun create(objectPool: ObjectPool, data: JsonElement): CreateInvitationWorkflow {
            val frameData = data.jsonObject["data"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), CreateInvitationData::class)
            } ?: CreateInvitationData()

            val sequence = data.jsonObject["sequence"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), SequenceFrame::class)
            } as SequenceFrame<BotContext, BotEvent>

            return CreateInvitationWorkflow(
                chatPermissionsService,
                invitationService,

                frameData,
                sequence
            )
        }

        fun createNew() = CreateInvitationWorkflow(
            chatPermissionsService,
            invitationService,
            findUserByIdFrameFactory,
        )
    }
}