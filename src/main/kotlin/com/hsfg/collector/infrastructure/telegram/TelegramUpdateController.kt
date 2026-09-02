package com.hsfg.collector.infrastructure.telegram

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.interaction.application.service.InteractionHandlerService
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Update
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class TelegramUpdateController(
    private val handlerService: InteractionHandlerService
) : UpdatesListener {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    override fun process(list: List<Update>): Int {
        for (update in list) {
            try {
                handleUpdate(update)
            } catch (e: Exception) {
                logger.error(e) { "Failed to handle user update $update" }
            }

        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL
    }

    private fun handleUpdate(update: Update) {
        if (update.message() != null) {
            val chatId = ChatId(update.message().chat().id().toString())

            val messageId = MessageId(update.message().messageId().toString())

            val messageBody = MessageBody(
                update.message().text(),
                attachments = emptyList(),
                replyTo = update.message().replyToMessage()?.messageId()?.let { MessageId(it.toString()) }
            )

            handlerService.handle(chatId, IncomingEvent.MessageReceived(messageId, messageBody))

        } else if (update.callbackQuery() != null) {
            val chatId = ChatId(update.callbackQuery().from().id().toString())

            val sourceMessageId = MessageId(
                update.callbackQuery().maybeInaccessibleMessage()?.messageId()?.toString()
                    ?: error("Source message ID is null")
            )

            val tag = update.callbackQuery().data()

            handlerService.handle(chatId, IncomingEvent.ButtonClicked(sourceMessageId, tag))
        }
    }
}
