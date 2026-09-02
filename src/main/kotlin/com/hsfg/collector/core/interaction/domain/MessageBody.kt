package com.hsfg.collector.core.interaction.domain.content

import com.hsfg.collector.core.interaction.domain.MessageId

data class MessageBody(
    val text: String,
    val attachments: List<MessageAttachment> = emptyList(),
    val replyTo: MessageId? = null
)
