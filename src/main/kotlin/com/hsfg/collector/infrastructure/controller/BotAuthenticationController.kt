package com.hsfg.collector.infrastructure.controller

import com.hsfg.collector.core.user.application.serivce.ChatAuthenticationService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
@RequestMapping("/bot/auth")
class BotAuthenticationController(
    private val chatAuthenticationService: ChatAuthenticationService,
) {

    @GetMapping("/callback")
    fun authCallback(@RequestParam state: String, @RequestParam code: String): String {
        chatAuthenticationService.login(state, code)

        return "auth/tg_authenticated"
    }
}