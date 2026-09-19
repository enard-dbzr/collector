package com.hsfg.collector.core.interaction.domain.content

sealed interface MessageAttachment {

    data class ButtonAttachment(
        val text: String,
        val tag: String? = null,
        val inlineUrl: String? = null,
    ) : MessageAttachment
}
