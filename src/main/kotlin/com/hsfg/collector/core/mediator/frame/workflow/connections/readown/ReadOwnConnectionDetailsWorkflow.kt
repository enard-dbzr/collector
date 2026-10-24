package com.hsfg.collector.core.mediator.frame.workflow.connections.readown

import com.hsfg.collector.core.connection.application.service.ConnectionDetailsService
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.user.application.serivce.ChatAuthenticationService
import com.hsfg.collector.core.user.application.serivce.ChatPermissionsService
import com.hsfg.collector.core.user.domain.UserPermission
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import org.springframework.stereotype.Component

class ReadOwnConnectionDetailsWorkflow(
    private val chatAuthenticationService: ChatAuthenticationService,
    private val chatPermissionsService: ChatPermissionsService,
    private val connectionDetailsService: ConnectionDetailsService,
) : Frame<BotContext, BotEvent, Nothing?> {

    override fun onEnter(context: BotContext): FrameResult<Nothing?> {
        if (validatePermissions(context).not()) {
            return FrameResult.Finished(null)
        }

        context.deliveryService.sendMessage(
            context.chatId,
            MessageBody(text = createMessage(context.chatId))
        )

        return FrameResult.Finished(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<Nothing?> {
        return FrameResult.Finished(null)
    }

    private fun validatePermissions(context: BotContext): Boolean {
        if (chatPermissionsService.check(context.chatId, UserPermission.READ_OWN_CONNECTIONS).not()) {
            context.deliveryService.sendMessage(
                context.chatId,
                MessageBody("У вас недостаточно прав. Пожалуйста, обратитесь к администратору.")
            )

            return false
        }

        return true
    }

    private fun createMessage(chatId: ChatId): String {
        val userId = chatAuthenticationService.getLocalUser(chatId).id
        val connections = connectionDetailsService.getUserConnections(userId)

        if (connections.isEmpty()) {
            return "У вас пока нет сохранённых подключений."
        }

        return buildString {
            appendLine("<b>Ваши подключения</b>")
            appendLine()

            connections.forEachIndexed { index, connection ->
                appendLine("${index + 1}. ${connection.name}")
                appendLine(connection.details)
                appendLine()
            }

            trimEnd()
        }
    }

    @Component
    class Factory(
        private val chatAuthenticationService: ChatAuthenticationService,
        private val chatPermissionsService: ChatPermissionsService,
        private val connectionDetailsService: ConnectionDetailsService,
    ) : DataFactory<ReadOwnConnectionDetailsWorkflow> {

        override fun serialize(objectPool: ObjectPool, instance: ReadOwnConnectionDetailsWorkflow): JsonElement {
            return buildJsonObject { }
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): ReadOwnConnectionDetailsWorkflow {
            return createNew()
        }

        fun createNew() = ReadOwnConnectionDetailsWorkflow(
            chatAuthenticationService,
            chatPermissionsService,
            connectionDetailsService,
        )
    }
}
