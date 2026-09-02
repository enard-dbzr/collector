package com.hsfg.collector.core.interaction.application.dto.incoming

import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.interaction.domain.content.MessageBody

sealed interface IncomingEvent {

    data class MessageReceived(
        val messageId: MessageId,
        val body: MessageBody,
    ) : IncomingEvent

    data class ButtonClicked(
        val source: MessageId,
        val tag: String,
    ) : IncomingEvent
}