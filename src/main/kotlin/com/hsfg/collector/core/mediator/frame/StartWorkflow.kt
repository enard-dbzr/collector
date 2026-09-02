package com.hsfg.collector.core.mediator.frame

import com.hsfg.collector.core.mediator.BotContext
import com.hsfg.collector.core.mediator.BotEvent
import com.hsfg.collector.core.workflow.application.frame.sequence.FrameKey
import com.hsfg.collector.core.workflow.application.frame.sequence.FrameStep
import com.hsfg.collector.core.workflow.application.frame.sequence.SequenceFrame
import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.*
import java.util.*

class StartWorkflow private constructor(
    private val sequence: SequenceFrame<BotContext, BotEvent>
) : Frame<BotContext, BotEvent, Nothing?> {

    constructor() : this(
        SequenceFrame(
            listOf(
                FrameStep(FrameKey(), SendMessageFrame("Hello")),
                FrameStep(ASK_NAME, AskFrame("Name?")),
                FrameStep(FrameKey(), SendMessageFrame("nice")),
                FrameStep(ASK_AGE, AskFrame("Age?")),
                FrameStep(FrameKey(), SendMessageFrame("nice2")),
                FrameStep(ASK_EMAIL, AskFrame("Email?")),
                FrameStep(FrameKey(), SendMessageFrame("done")),
            )
        )
    )

    companion object {
        private val logger = KotlinLogging.logger { }

        private val ASK_NAME = FrameKey<String?>("ask-name")
        private val ASK_AGE = FrameKey<String?>("ask-age")
        private val ASK_EMAIL = FrameKey<String?>("ask-email")
    }

    override fun onEnter(context: BotContext): FrameResult<Nothing?> {
        val result = sequence.onEnter(context)

        processSequenceResult(result.value)

        return FrameResult.Continue(null)
    }

    override fun handle(context: BotContext, event: BotEvent): FrameResult<Nothing?> {
        val result = sequence.handle(context, event)

        processSequenceResult(result.value)

        return when (result) {
            is FrameResult.Continue -> FrameResult.Continue(null)
            is FrameResult.Finished -> FrameResult.Finished(null)
        }
    }

    override fun onExit(context: BotContext) {
        sequence.onExit(context)
    }

    private fun processSequenceResult(result: SequenceFrame.Result) {
        logger.info { "Got sequence result: $result" }

        result.results(ASK_NAME).forEach {
            logger.info { "Got name: ${it.value}" }
        }

        result.results(ASK_AGE).forEach {
            logger.info { "Got age: ${it.value}" }
        }

        result.results(ASK_EMAIL).forEach {
            logger.info { "Got email: ${it.value}" }
        }
    }

    class StartWorkflowFactory : DataFactory<StartWorkflow> {
        override fun serialize(objectPool: ObjectPool, instance: StartWorkflow): JsonElement {
            return buildJsonObject {
                put("sequence", objectPool.put(instance.sequence).value.toString())
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun create(objectPool: ObjectPool, data: JsonElement): StartWorkflow {
            val sequenceId = UUID.fromString(data.jsonObject["sequence"]!!.jsonPrimitive.content)
            val sequence = objectPool.getData(PoolId(sequenceId), SequenceFrame::class)
                    as SequenceFrame<BotContext, BotEvent>

            return StartWorkflow(sequence)
        }
    }
}