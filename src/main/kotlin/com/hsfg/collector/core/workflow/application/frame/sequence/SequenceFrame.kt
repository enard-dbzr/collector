package com.hsfg.collector.core.workflow.application.frame.sequence

import com.hsfg.collector.core.workflow.domain.frame.Frame
import com.hsfg.collector.core.workflow.domain.frame.FrameContext
import com.hsfg.collector.core.workflow.domain.frame.FrameEvent
import com.hsfg.collector.core.workflow.domain.frame.FrameResult
import com.hsfg.collector.core.workflow.domain.objectpool.DataFactory
import com.hsfg.collector.core.workflow.domain.objectpool.ObjectPool
import com.hsfg.collector.core.workflow.domain.objectpool.PoolId
import kotlinx.serialization.json.*
import java.util.*

class SequenceFrame<C : FrameContext, E : FrameEvent>(
    steps: List<FrameStep<C, E, *>>
) : Frame<C, E, SequenceFrame.Result> {

    private val steps: MutableList<RuntimeStep<C, E>> =
        steps.map { RuntimeStep(it.key.id, it.frame) }.toMutableList()

    override fun onEnter(context: C): FrameResult<Result> {
        val enterResult = steps.first().frame.onEnter(context)

        return changeStateAttempt(context, enterResult)
    }

    override fun handle(context: C, event: E): FrameResult<Result> {
        val handleResult = steps.first().frame.handle(context, event)

        return changeStateAttempt(context, handleResult)
    }

    override fun onExit(context: C) {
        steps.firstOrNull()?.frame?.onExit(context)
    }

    private fun changeStateAttempt(
        context: C,
        currentResult: FrameResult<*>,
        collected: MutableList<ResultItem<*>> = mutableListOf()
    ): FrameResult<Result> {

        val current = steps.first()
        collected.add(ResultItem(current.key, currentResult))

        if (currentResult is FrameResult.Finished) {
            steps.removeFirst()
            current.frame.onExit(context)

            if (steps.isEmpty()) {
                return FrameResult.Finished(Result(collected))
            } else {
                val enterResult = steps.first().frame.onEnter(context)
                return changeStateAttempt(context, enterResult, collected)
            }
        }

        return FrameResult.Continue(Result(collected))
    }

    data class ResultItem<out R>(
        val key: String?,
        val result: FrameResult<R>
    )

    data class Result(val r: List<ResultItem<*>>) {

        @Suppress("UNCHECKED_CAST")
        fun <R> results(key: FrameKey<R>): List<FrameResult<R>> {
            key.id ?: error("FrameKey id is null")

            return r
                .filter { it.key == key.id }
                .map { it.result as FrameResult<R> }
        }
    }

    private data class RuntimeStep<C : FrameContext, E : FrameEvent>(
        val key: String?,
        val frame: Frame<C, E, *>,
    )

    class SequenceFrameFactory : DataFactory<SequenceFrame<*, *>> {

        override fun serialize(objectPool: ObjectPool, instance: SequenceFrame<*, *>): JsonElement {

            return buildJsonObject {
                put(
                    "steps",
                    buildJsonArray {
                        instance.steps
                            .forEach { (key, frame) ->
                                add(buildJsonObject {
                                    put("key", key)
                                    put("frame", objectPool.put(frame).value.toString())
                                })
                            }
                    },
                )
            }
        }

        override fun create(objectPool: ObjectPool, data: JsonElement): SequenceFrame<*, *> {
            val stepNodes = data.jsonObject["steps"]?.jsonArray
                ?: error("Missing 'steps' field in SequenceFrame data")

            val steps: List<FrameStep<*, *, *>> = stepNodes.map { node ->
                val objectNode = node.jsonObject

                val keyId = objectNode["key"]?.jsonPrimitive?.content
                    ?: error("Missing 'key' field in SequenceFrame step")

                val frameId = objectNode["frame"]?.jsonPrimitive?.content
                    ?: error("Missing 'frame' field in SequenceFrame step")

                val frame = objectPool.getData<Frame<*, *, *>>(
                    PoolId(UUID.fromString(frameId)),
                ) as Frame<*, *, *>

                FrameStep(
                    key = FrameKey(keyId),
                    frame = frame,
                )
            }

            return SequenceFrame(steps)
        }
    }
}