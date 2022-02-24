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

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.security.core.userdetails.UserDetails
import java.time.Clock
import java.time.temporal.ChronoUnit
import java.util.Date

class JwtSupport(private val clock: Clock, private val jwtConfiguration: JwtConfiguration) {

    private val issuer: String = jwtConfiguration.issuer
    private val algorithmHS: Algorithm = Algorithm.HMAC256(jwtConfiguration.secret)
    private val verifier: JWTVerifier = JWT.require(algorithmHS)
        .withIssuer(issuer)
        .build()

    fun generate(username: String, roles: Array<String> = arrayOf()): BearerToken {
        val token = JWT.create()
            .withIssuer(issuer)
            .withSubject(username)
            .withIssuedAt(now())
            .withExpiresAt(nowPlus(jwtConfiguration.tokenLifetime))
            .withArrayClaim("roles", roles)
            .sign(algorithmHS)
        return BearerToken(token)
    }

    fun getUsername(token: BearerToken): String {
        val jwt: DecodedJWT = verifier.verify(token.value)
        return jwt.subject
    }

    fun isValid(token: BearerToken, user: UserDetails?): Boolean =
        try {
            val jwt: DecodedJWT = verifier.verify(token.value)
            jwt.subject == user?.username
        } catch (tokenExpiredException: TokenExpiredException) {
            false
        }

    private fun now(): Date = Date.from(clock.instant())

    private fun nowPlus(amountToAdd: Long): Date = Date.from(clock.instant().plus(amountToAdd, ChronoUnit.SECONDS))
}
