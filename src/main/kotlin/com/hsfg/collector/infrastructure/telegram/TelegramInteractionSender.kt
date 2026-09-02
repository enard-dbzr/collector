package com.hsfg.collector.infrastructure.telegram

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.model.request.ReplyParameters
import com.pengrad.telegrambot.request.SendMessage
import com.hsfg.collector.core.interaction.application.port.out.InteractionSenderPort
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageAttachment
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import org.springframework.stereotype.Service


@Service
class TelegramInteractionSender(
    private val bot: TelegramBot
) : InteractionSenderPort {

    override fun sendMessage(chatId: ChatId, body: MessageBody): MessageId {
        val request = SendMessage(chatId.value.toLong(), body.text)
            .parseMode(ParseMode.Markdown)

        if (body.replyTo != null)
            request.replyParameters(ReplyParameters(body.replyTo.value.toInt()))

        if (body.attachments.any { it is MessageAttachment.ButtonAttachment })
            request.replyMarkup(buildInlineKeyboard(body.attachments))

        val response = bot.execute(request)

        if (response.isOk) {
            return MessageId(response.message().messageId().toString())
        } else {
            throw RuntimeException("Failed to send message: ${response.description()}")
        }

    }

    private fun buildInlineKeyboard(attachments: List<MessageAttachment>): InlineKeyboardMarkup {
        val markup = InlineKeyboardMarkup()

        attachments
            .filterIsInstance<MessageAttachment.ButtonAttachment>()
            .map { InlineKeyboardButton(it.text, it.tag) }
            .forEach { markup.addRow(it) }

        return markup
    }


}
