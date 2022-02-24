/*
 *   Copyright (c) 2021-2022 (C) Carlo Micieli
 *
 *    Licensed to the Apache Software Foundation (ASF) under one
 *    or more contributor license agreements.  See the NOTICE file
 *    distributed with this work for additional information
 *    regarding copyright ownership.  The ASF licenses this file
 *    to you under the Apache License, Version 2.0 (the
 *    "License"); you may not use this file except in compliance
 *    with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing,
 *    software distributed under the License is distributed on an
 *    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *    KIND, either express or implied.  See the License for the
 *    specific language governing permissions and limitations
 *    under the License.    
 */
package io.github.carlomicieli.webapi.authentication

import io.github.carlomicieli.infrastructure.security.AuthenticatedUser
import io.github.carlomicieli.infrastructure.security.JwtAuthenticationService
import io.github.carlomicieli.infrastructure.security.Unauthorized
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.context.support.beans
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter

object Authentication {

    val beans = beans {
        bean {
            val jwtAuthenticationService = ref<JwtAuthenticationService>()
            AuthHandler(jwtAuthenticationService)
        }

        bean {
            val authHandler = ref<AuthHandler>()

            coRouter {
                "/auth".nest {
                    accept(MediaType.APPLICATION_JSON).nest {
                        POST("/login", authHandler::login)
                        POST("/register", authHandler::register)
                    }
                    GET("", authHandler::authenticated)
                }
            }
        }
    }
}

class AuthHandler(private val jwtAuthenticationService: JwtAuthenticationService) {
    suspend fun login(request: ServerRequest): ServerResponse {
        val login = request.awaitBody<Login>()
        return when (val result = jwtAuthenticationService.authenticate(login.username, login.password)) {
            is AuthenticatedUser -> ServerResponse.ok().bodyValue(JwtToken(result.token)).awaitSingle()
            is Unauthorized -> ServerResponse.status(HttpStatus.UNAUTHORIZED).buildAndAwait()
        }
    }

    suspend fun register(request: ServerRequest): ServerResponse {
        val profile = request.awaitBody<Profile>()
        val token = jwtAuthenticationService.register(profile.username, profile.password)
        return ServerResponse.accepted().header(HttpHeaders.AUTHORIZATION, token).buildAndAwait()
    }

    suspend fun authenticated(request: ServerRequest): ServerResponse {
        val principal = request.principal().awaitSingle()
        val token = jwtAuthenticationService.generateToken(principal.name)
        return ServerResponse.ok().bodyValue(token).awaitSingle()
    }
}

data class Profile(
    val username: String,
    val password: String
)

data class JwtToken(val token: String)

data class Login(
    val username: String,
    val password: String
)
