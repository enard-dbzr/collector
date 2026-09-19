package com.hsfg.collector.infrastructure.telegram

import com.hsfg.collector.core.interaction.application.dto.outgoing.EditMessageBody
import com.hsfg.collector.core.interaction.application.port.out.InteractionSenderPort
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageAttachment
import com.hsfg.collector.core.interaction.domain.content.MessageBody
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.WebAppInfo
import com.pengrad.telegrambot.model.request.InlineKeyboardButton
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.model.request.ReplyParameters
import com.pengrad.telegrambot.request.EditMessageReplyMarkup
import com.pengrad.telegrambot.request.EditMessageText
import com.pengrad.telegrambot.request.SendMessage
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
            return composeMessageId(chatId.value.toLong(), response.message().messageId())
        } else {
            throw RuntimeException("Failed to send message: ${response.description()}")
        }
    }

    override fun editMessage(
        messageId: MessageId,
        body: EditMessageBody
    ) {
        val request = if (body.text != null) {
            val r = EditMessageText(messageId.chatId(), messageId.messageId(), body.text).parseMode(ParseMode.Markdown)
            body.attachments?.let { a -> r.replyMarkup(buildInlineKeyboard(a)) }
            r
        } else {
            val r = EditMessageReplyMarkup(messageId.chatId(), messageId.messageId())
            body.attachments?.let { a -> r.replyMarkup(buildInlineKeyboard(a)) }
            r
        }

        val response = bot.execute(request)

        if (!response.isOk) {
            throw RuntimeException("Failed to edit message: ${response.description()}")
        }
    }

    private fun buildInlineKeyboard(attachments: List<MessageAttachment>): InlineKeyboardMarkup {
        val markup = InlineKeyboardMarkup()

        attachments
            .filterIsInstance<MessageAttachment.ButtonAttachment>()
            .map { InlineKeyboardButton(it.text, callbackData = it.tag, webApp = WebAppInfo(it.inlineUrl)) }
            .forEach { markup.addRow(it) }

        return markup
    }

    private fun MessageId.chatId() = this.value.split(":")[0].toLong()

    private fun MessageId.messageId() = this.value.split(":")[1].toInt()

    private fun composeMessageId(chatId: Long, messageId: Int) = MessageId("$chatId:$messageId")
}
