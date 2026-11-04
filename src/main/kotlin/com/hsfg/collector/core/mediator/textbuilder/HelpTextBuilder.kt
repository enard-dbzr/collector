package com.hsfg.collector.core.mediator.textbuilder

import com.hsfg.collector.core.mediator.textbuilder.config.SubstitutionProperties
import org.springframework.stereotype.Component

@Component
class HelpTextBuilder(
    private val substitutionProperties: SubstitutionProperties
) {

    fun buildText(): String {
        return (
                "Доступные команды:\n\n" +
                        "/connections — просмотр одобренных подключений.\n" +
                        "/help — справка по командам.\n\n" +
                        "Анонсы, новости и другая важная информация будут приходить в этот бот.\n\n" +
                        "Инструкции, ответы на вопросы и общение с другими пользователями доступны в чате %s."
                ).format(substitutionProperties.helpChatLink)
    }
}