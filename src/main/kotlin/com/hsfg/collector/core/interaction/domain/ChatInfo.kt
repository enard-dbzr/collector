package com.hsfg.collector.core.interaction.domain

data class ChatInfo(
    val chatId: ChatId,
    val firstName: String,
    val username: String?,
)