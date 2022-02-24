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
package io.github.carlomicieli.infrastructure.security

import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder

class JwtAuthenticationService(
    private val jwtSupport: JwtSupport,
    private val encoder: PasswordEncoder,
    private val usersService: UsersService
) {
    companion object {
        val LOG = LoggerFactory.getLogger(JwtAuthenticationService::class.java)
    }

    suspend fun authenticate(username: String, password: String): Authentication {
        val user = usersService.findByUsername(username)

        return if (user != null && encoder.matches(password, user.password)) {
            val roles = user.authorities.map { it.authority.lowercase() }.toTypedArray()
            AuthenticatedUser(jwtSupport.generate(user.username, roles).value)
        } else {
            Unauthorized
        }
    }

    suspend fun register(username: String, password: String): String {
        return try {
            val created = usersService.registerUser(username, password, listOf("USER"))
            jwtSupport.generate(username, created.authorities.map { it.authority }.toTypedArray()).value
        } catch (ex: Exception) {
            LOG.error("An error occurred during a new user registration", ex)
            ""
        }
    }

    fun generateToken(username: String): Authentication {
        return AuthenticatedUser(jwtSupport.generate(username).value)
    }
}

sealed interface Authentication
data class AuthenticatedUser(val token: String) : Authentication
object Unauthorized : Authentication
