package com.hsfg.collector.core.mediator.textbuilder

import com.hsfg.collector.core.mediator.textbuilder.config.SubstitutionProperties
import org.springframework.stereotype.Component

@Component
class StartTextBuilder(
    private val substitutionProperties: SubstitutionProperties
) {

    fun buildText(): String {
        return (
                "Добро пожаловать!\n\n" +
                        "После одобрения модератором ваши подключения станут доступны для просмотра. Вы получите уведомление, когда они появятся.\n\n" +
                        "Используйте /connections, чтобы просмотреть список подключений.\n\n" +
                        "Анонсы, новости и другая важная информация будут приходить от этого бота. Инструкции, ответы на вопросы и общение с другими пользователями доступны в чате %s.\n\n" +
                        "Список команд: /help."
                ).format(substitutionProperties.helpChatLink)
    }
}