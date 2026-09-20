package com.hsfg.collector.core.mediator

import com.hsfg.collector.core.interaction.application.port.out.IncomingEventProcessor
import com.hsfg.collector.core.interaction.application.service.InteractionDeliveryService
import com.hsfg.collector.core.interaction.domain.ChatId
import com.hsfg.collector.core.mediator.frame.RootFrame
import com.hsfg.collector.core.workflow.application.WorkflowStorageService
import org.springframework.stereotype.Service

@Service
class MediatorEventProcessor(
    private val workflowStorage: WorkflowStorageService,
    private val deliveryService: InteractionDeliveryService,
    private val rootFrameFactory: RootFrame.RootFrameFactory,
) : IncomingEventProcessor {

    override fun process(
        chatId: ChatId,
        event: BotEvent
    ) {

        val root = workflowStorage.load(chatId.value, RootFrame::class) ?: rootFrameFactory.createNew()

        val context = BotContext(deliveryService, chatId)

        root.handle(context, event)

        workflowStorage.store(chatId.value, root)
    }


}