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

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import reactor.test.StepVerifier
import java.time.Clock

@DisplayName("JwtAuthenticationManager")
class JwtAuthenticationManagerTest {

    private val jwtSupport = jwtSupport()
    private val usersService: UsersService = TestUsersService()
    private val jwtAuthenticationManager = JwtAuthenticationManager(jwtSupport, usersService)

    @Test
    fun `should authenticate valid bearer tokens`() {
        val token = jwtSupport.generate(validUser.username)

        val result = jwtAuthenticationManager.authenticate(token)

        StepVerifier
            .create(result)
            .expectNext(UsernamePasswordAuthenticationToken(validUser.username, validUser.password, listOf()))
            .expectComplete()
            .log()
            .verify()
    }

    @Test
    fun `should fail to authenticate tokens with an invalid secret key`() {
        val anotherJwtSupport = jwtSupport("different secret key")
        val token = anotherJwtSupport.generate("user")

        val result = jwtAuthenticationManager.authenticate(token)

        StepVerifier.create(result)
            .expectError(InvalidBearerToken::class.java)
            .verify()
    }

    @Test
    fun `should fail to authenticate expired tokens`() {
        val anotherJwtSupport = JwtSupport(Clock.systemUTC(), JwtConfiguration("testapp", -1, "my super secret key"))
        val token = anotherJwtSupport.generate("user")

        val result = jwtAuthenticationManager.authenticate(token)

        StepVerifier.create(result)
            .expectError(InvalidBearerToken::class.java)
            .verify()
    }

    @Test
    fun `should fail to authenticate tokens when the user account is not found`() {
        val token = jwtSupport.generate("user-not-found")

        val result = jwtAuthenticationManager.authenticate(token)

        StepVerifier.create(result)
            .expectError(InvalidBearerToken::class.java)
            .verify()
    }
}
