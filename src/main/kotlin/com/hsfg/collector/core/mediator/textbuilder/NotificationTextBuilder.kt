package com.hsfg.collector.core.mediator.textbuilder

import com.hsfg.collector.core.mediator.notification.ChatNotification
import com.hsfg.collector.core.mediator.notification.ConnectionsUpdatedNotification
import com.hsfg.collector.core.mediator.notification.UserRegisteredNotification
import org.springframework.stereotype.Component

@Component
class NotificationTextBuilder(
    private val userProfileTextBuilder: UserProfileTextBuilder,
) {

    fun buildText(notification: ChatNotification): String {
        return when (notification) {
            is ConnectionsUpdatedNotification -> buildConnectionsUpdatedText()
            is UserRegisteredNotification -> buildUserRegisteredText(notification)
        }
    }

    private fun buildConnectionsUpdatedText() = buildString {
        append("Ваши подключения были обновлены. Проверить актуальный список можно с помощью команды /connections")
    }

    private fun buildUserRegisteredText(notification: UserRegisteredNotification): String {
        return userProfileTextBuilder.buildProfile(
            notification.userId,
            "Зарегистрирован новый пользователь"
        )
    }
}