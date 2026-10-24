package com.hsfg.collector.core.mediator.config

import com.hsfg.collector.core.interaction.domain.MessageId
import com.hsfg.collector.core.mediator.datafactory.MessageIdFactory
import com.hsfg.collector.core.mediator.datafactory.UserIdFactory
import com.hsfg.collector.core.mediator.frame.RootFrame
import com.hsfg.collector.core.mediator.frame.utils.AuthFrame
import com.hsfg.collector.core.mediator.frame.utils.ProcessNotificationEventFrame
import com.hsfg.collector.core.mediator.frame.utils.ask.AskTextFrame
import com.hsfg.collector.core.mediator.frame.utils.message.SendMessageFrame
import com.hsfg.collector.core.mediator.frame.utils.user.find.FindUserByIdFrame
import com.hsfg.collector.core.mediator.frame.workflow.StartWorkflow
import com.hsfg.collector.core.mediator.frame.workflow.connections.create.CreateConnectionDetailsData
import com.hsfg.collector.core.mediator.frame.workflow.connections.create.CreateConnectionDetailsWorkflow
import com.hsfg.collector.core.mediator.frame.workflow.connections.readown.ReadOwnConnectionDetailsWorkflow
import com.hsfg.collector.core.mediator.frame.workflow.invitation.create.CreateInvitationData
import com.hsfg.collector.core.mediator.frame.workflow.invitation.create.CreateInvitationWorkflow
import com.hsfg.collector.core.user.domain.UserId
import com.hsfg.collector.core.workflow.application.DefaultDataFactoryRegistry
import com.hsfg.collector.core.workflow.application.frame.sequence.SequenceFrame
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataFactoryConfig(
    private val rootFrameFactory: RootFrame.RootFrameFactory,
    private val authFrameFactory: AuthFrame.AuthFrameFactory,
    private val findUserByIdFrameFactory: FindUserByIdFrame.Factory,
    private val processNotificationEventFrameFactory: ProcessNotificationEventFrame.Factory,

    private val startWorkflowFactory: StartWorkflow.StartWorkflowFactory,
    private val createConnectionDetailsWorkflowFactory: CreateConnectionDetailsWorkflow.Factory,
    private val readOwnConnectionDetailsWorkflowFactory: ReadOwnConnectionDetailsWorkflow.Factory,
    private val createInvitationWorkflowFactory: CreateInvitationWorkflow.Factory,
) {
    @Bean
    fun defaultDataFactoryRegistry(): DefaultDataFactoryRegistry {
        val registry = DefaultDataFactoryRegistry()

        registry.register("frame.root", RootFrame::class, rootFrameFactory)
        registry.register("frame.sequence", SequenceFrame::class, SequenceFrame.SequenceFrameFactory())


        registry.register(
            "frame.utils.message.send_message",
            SendMessageFrame::class,
            SendMessageFrame.SendMessageFrameFactory()
        )
        registry.register("frame.utils.ask.text", AskTextFrame::class, AskTextFrame.Factory())
        registry.register("frame.utils.user.find.by_id", FindUserByIdFrame::class, findUserByIdFrameFactory)
        registry.register("frame.utils.auth", AuthFrame::class, authFrameFactory)
        registry.register(
            "frame.utils.processor.event",
            ProcessNotificationEventFrame::class,
            processNotificationEventFrameFactory
        )


        registry.register(
            "frame.workflow.start",
            StartWorkflow::class,
            startWorkflowFactory
        )

        registry.register(
            "frame.workflow.connections.create",
            CreateConnectionDetailsWorkflow::class,
            createConnectionDetailsWorkflowFactory
        )
        registry.register(
            "frame.workflow.connections.create:data",
            CreateConnectionDetailsData::class,
            CreateConnectionDetailsData.Factory()
        )
        registry.register(
            "frame.workflow.connections.read_own",
            ReadOwnConnectionDetailsWorkflow::class,
            readOwnConnectionDetailsWorkflowFactory
        )

        registry.register(
            "frame.workflow.inivitaion.create",
            CreateInvitationWorkflow::class,
            createInvitationWorkflowFactory
        )
        registry.register(
            "frame.workflow.inivitaion.create:data",
            CreateInvitationData::class,
            CreateInvitationData.Factory()
        )

        registry.register("data.utils.message_id", MessageId::class, MessageIdFactory())
        registry.register("data.utils.user_id", UserId::class, UserIdFactory())

        return registry
    }
}