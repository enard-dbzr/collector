package com.hsfg.collector.core.user.application.exception

import com.hsfg.collector.core.interaction.domain.ChatId

class ChatUnauthorizedException(val chatId: ChatId) :
    Exception("Chat with id ${chatId.value} is not authorized to perform this action")