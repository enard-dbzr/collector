package com.hsfg.collector.core.mediator.mapper

import com.hsfg.collector.core.mediator.notification.ChatNotification
import com.hsfg.collector.core.mediator.notification.ConnectionsUpdatedNotification
import org.springframework.stereotype.Component

@Component
class NotificationTextMapper {

    fun buildText(notification: ChatNotification): String {
        return when (notification) {
            is ConnectionsUpdatedNotification -> buildConnectionsUpdatedText()
        }
    }

    private fun buildConnectionsUpdatedText() = buildString {
        append("Ваши подключения были обновлены. Проверить актуальный список можно с помощью команды /connections")
    }
}