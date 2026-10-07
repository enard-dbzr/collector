package com.hsfg.collector.infrastructure.telegram

import com.hsfg.collector.core.interaction.domain.MessageId

fun composeMessageId(chatId: Long, messageId: Int) = MessageId("$chatId:$messageId")

fun MessageId.chatId() = this.value.split(":")[0].toLong()

fun MessageId.messageId() = this.value.split(":")[1].toInt()