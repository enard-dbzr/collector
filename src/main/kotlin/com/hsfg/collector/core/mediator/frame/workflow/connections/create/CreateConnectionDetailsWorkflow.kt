package com.hsfg.collector.core.mediator.frame.workflow.connections.create

import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.mediator.frame.utils.ask.AskTextFrame
import com.hsfg.collector.core.user.application.serivce.ChatPermissionsService
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

class CreateConnectionDetailsWorkflow private constructor(
    private val chatPermissionsService: ChatPermissionsService,

    private val sequence: SequenceFrame<BotContext, BotEvent>,
    private val data: CreateConnectionDetailsData
) : Frame<BotContext, BotEvent, Any?> {

    companion object {
        val log = KotlinLogging.logger { }

        val ASK_NAME_KEY = FrameKey<String?>("ask_name")
        val ASK_DETAILS_KEY = FrameKey<String?>("ask_details")
        val ASK_NOTES_KEY = FrameKey<String?>("ask_notes")
    }

    constructor(chatPermissionsService: ChatPermissionsService) : this(
        chatPermissionsService,

        SequenceFrame(
            listOf(
                FrameStep(
                    ASK_NAME_KEY,
                    AskTextFrame("Введите название подключения")
                ),
                FrameStep(
                    ASK_DETAILS_KEY,
                    AskTextFrame(
                        "Введите детали подключения (например, URL, логин, пароль и т.д.)"
                    )
                ),
                FrameStep(
                    ASK_NOTES_KEY,
                    AskTextFrame(
                        "Введите заметки к подключению (необязательно)",
                        skippable = true,
                        messageAfterSkip = "Вы пропустили ввод заметок"
                    )
                )
            )
        ),
        CreateConnectionDetailsData()
    )

    override fun onEnter(context: BotContext): FrameResult<*> {
        if (validatePermissions(context).not()) {
            return FrameResult.Finished(null)
        }

        return sequence.onEnter(context)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<*> {
        val result = sequence.handle(context, event)

        result.value.results(ASK_NAME_KEY).mapNotNull { it.value }.forEach {
            data.name = it
        }

        result.value.results(ASK_DETAILS_KEY).mapNotNull { it.value }.forEach {
            data.details = it
        }

        result.value.results(ASK_NOTES_KEY).mapNotNull { it.value }.forEach {
            data.notes = it
        }

        if (result is FrameResult.Finished) {
            if (validatePermissions(context).not()) {
                return FrameResult.Finished(null)
            }

            log.info { "Created connection details workflow finished for chat with id ${context.chatId}: $data" }
        }

        return result
    }

    override fun onExit(context: BotContext) {
        sequence.onExit(context)
    }

    private fun validatePermissions(context: BotContext): Boolean {
        if (chatPermissionsService.check(context.chatId, UserPermission.WRITE_CONNECTIONS).not()) {
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
    ) : DataFactory<CreateConnectionDetailsWorkflow> {
        override fun serialize(objectPool: ObjectPool, instance: CreateConnectionDetailsWorkflow): JsonElement {
            return buildJsonObject {
                put("sequence", objectPool.put(instance.sequence).value.toString())
                put("data", objectPool.put(instance.data).value.toString())
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun create(objectPool: ObjectPool, data: JsonElement): CreateConnectionDetailsWorkflow {
            val sequence = data.jsonObject["sequence"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), SequenceFrame::class)
            } as SequenceFrame<BotContext, BotEvent>

            val data = data.jsonObject["data"]?.jsonPrimitive?.contentOrNull?.let {
                objectPool.getData(PoolId(UUID.fromString(it)), CreateConnectionDetailsData::class)
            }

            return CreateConnectionDetailsWorkflow(
                chatPermissionsService,

                sequence,
                data ?: CreateConnectionDetailsData()
            )
        }

        fun createNew() = CreateConnectionDetailsWorkflow(
            chatPermissionsService
        )
    }
}