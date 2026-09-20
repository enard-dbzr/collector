package com.hsfg.collector.core.mediator

import com.hsfg.collector.core.interaction.application.dto.incoming.IncomingEvent
import com.hsfg.collector.core.workflow.domain.frame.FrameEvent

sealed interface BotEvent : FrameEvent {

    data class ChatHandled(val event: IncomingEvent) : BotEvent

    class Authorized : BotEvent
}
