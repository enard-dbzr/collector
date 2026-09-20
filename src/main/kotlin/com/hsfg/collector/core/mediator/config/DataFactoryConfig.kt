package com.hsfg.collector.core.mediator.config

import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.mediator.datafactory.MessageIdFactory
import com.hsfg.collector.core.mediator.frame.AskFrame
import com.hsfg.collector.core.mediator.frame.utils.AuthFrame
import com.hsfg.collector.core.mediator.frame.RootFrame
import com.hsfg.collector.core.mediator.frame.SendMessageFrame
import com.hsfg.collector.core.mediator.frame.StartWorkflow
import com.hsfg.collector.core.workflow.application.DefaultDataFactoryRegistry
import com.hsfg.collector.core.workflow.application.frame.sequence.SequenceFrame
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataFactoryConfig(
    private val rootFrameFactory: RootFrame.RootFrameFactory,
    private val authFrameFactory: AuthFrame.AuthFrameFactory
) {
    @Bean
    fun defaultDataFactoryRegistry(): DefaultDataFactoryRegistry {
        val registry = DefaultDataFactoryRegistry()

        registry.register("frame.root", RootFrame::class, rootFrameFactory)
        registry.register("frame.sequence", SequenceFrame::class, SequenceFrame.SequenceFrameFactory())
        registry.register("frame.utils.send_message", SendMessageFrame::class, SendMessageFrame.SendMessageFrameFactory())
        registry.register("frame.utils.ask.message", AskFrame::class, AskFrame.AskFrameFactory())
        registry.register("frame.utils.auth", AuthFrame::class, authFrameFactory)

        registry.register("frame.workflow.start", StartWorkflow::class, StartWorkflow.StartWorkflowFactory())


        registry.register("data.utils.message_id", MessageId::class, MessageIdFactory())

        return registry
    }
}