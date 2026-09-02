package com.hsfg.collector.core.mediator.config

import com.hsfg.collector.core.mediator.frame.AskFrame
import com.hsfg.collector.core.mediator.frame.RootFrame
import com.hsfg.collector.core.mediator.frame.SendMessageFrame
import com.hsfg.collector.core.mediator.frame.StartWorkflow
import com.hsfg.collector.core.workflow.application.DefaultDataFactoryRegistry
import com.hsfg.collector.core.workflow.application.frame.sequence.SequenceFrame
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataFactoryConfig {
    @Bean
    fun defaultDataFactoryRegistry(): DefaultDataFactoryRegistry {
        val registry = DefaultDataFactoryRegistry()

        registry.register("frame.root", RootFrame::class, RootFrame.RootFrameFactory())
        registry.register("frame.sequence", SequenceFrame::class, SequenceFrame.SequenceFrameFactory())
        registry.register("frame.utils.send_message", SendMessageFrame::class, SendMessageFrame.SendMessageFrameFactory())
        registry.register("frame.utils.ask.message", AskFrame::class, AskFrame.AskFrameFactory())

        registry.register("frame.workflow.start", StartWorkflow::class, StartWorkflow.StartWorkflowFactory())

        return registry
    }
}