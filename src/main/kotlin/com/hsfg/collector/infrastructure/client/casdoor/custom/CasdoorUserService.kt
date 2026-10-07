package com.hsfg.collector.infrastructure.client.casdoor.custom

import com.fasterxml.jackson.core.type.TypeReference
import org.casbin.casdoor.config.Config
import org.casbin.casdoor.entity.User
import org.casbin.casdoor.service.UserService
import org.casbin.casdoor.util.http.CasdoorResponse

class CasdoorUserService(config: Config) : UserService(config) {
    fun getUserById(id: String): User? {
        val resp = doGet(
            "get-user",
            mapOf("userId" to id),
            object : TypeReference<CasdoorResponse<User?, Any?>?>() {}
        )

        return objectMapper.convertValue(resp.getData(), User::class.java)
    }
}