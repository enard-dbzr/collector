package com.hsfg.collector.core.mediator

import com.hsfg.collector.core.interaction.application.service.InteractionDeliveryService
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.workflow.domain.frame.FrameContext

data class BotContext(
    val deliveryService: InteractionDeliveryService,
    val chatId: ChatId
) : FrameContext
