package com.hsfg.collector.core.interaction.application.dto.outgoing

import com.hsfg.collector.core.interaction.domain.content.MessageAttachment

data class EditMessageBody(
    val text: String? = null,
    val attachments: List<MessageAttachment>? = null
)
