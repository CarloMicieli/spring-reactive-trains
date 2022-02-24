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

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Clock

@DisplayName("JwtAuthenticationService")
class JwtAuthenticationServiceTest {
    private val jwtSupport = JwtSupport(Clock.systemUTC(), JwtConfiguration("testapp", 900, "my super secret key"))
    private val encoder: PasswordEncoder = BCryptPasswordEncoder()
    private val usersService: UsersService = TestUsersService()
    private val jwtAuthenticationService: JwtAuthenticationService = JwtAuthenticationService(jwtSupport, encoder, usersService)

    @Test
    fun `first test`() = runBlocking {
        val f: Authentication = jwtAuthenticationService.authenticate("user", "password")
        (f is AuthenticatedUser) shouldBe true
    }
}
