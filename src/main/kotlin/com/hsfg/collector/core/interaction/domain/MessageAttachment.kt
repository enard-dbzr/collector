package com.hsfg.collector.core.interaction.domain.content

sealed interface MessageAttachment {

    data class ButtonAttachment(val text: String, val tag: String) : MessageAttachment
}
