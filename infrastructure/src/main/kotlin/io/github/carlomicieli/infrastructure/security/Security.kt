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

import org.springframework.context.support.beans
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import reactor.core.publisher.Mono
import java.time.Clock

object Security {
    val beans = beans {

        bean<PasswordEncoder> {
            BCryptPasswordEncoder()
        }

        bean<UsersService> {
            val usersRepository = ref<CoroutineUsersRepository>()
            val passwordEncoder = ref<PasswordEncoder>()
            R2dbcUserDetailsService(usersRepository, passwordEncoder)
        }

        bean<JwtServerAuthenticationConverter>()

        bean {
            val clock = ref<Clock>()
            val jwtConfiguration = ref<JwtConfiguration>()
            JwtSupport(clock, jwtConfiguration)
        }

        bean {
            val jwtSupport = ref<JwtSupport>()
            val usersService = ref<UsersService>()
            JwtAuthenticationManager(jwtSupport, usersService)
        }

        bean {
            val jwtSupport = ref<JwtSupport>()
            val encoder = ref<PasswordEncoder>()
            val usersService = ref<UsersService>()
            JwtAuthenticationService(jwtSupport, encoder, usersService)
        }

        bean {
            val converter = ref<JwtServerAuthenticationConverter>()
            val authManager = ref<JwtAuthenticationManager>()
            val filter = AuthenticationWebFilter(authManager)
            filter.setServerAuthenticationConverter(converter)

            val http = ref<ServerHttpSecurity>()

            http {
                authorizeExchange {
                    authorize("/auth/login", permitAll)
                    authorize("/auth/register", permitAll)
                    authorize("/health", permitAll)
                    authorize(anyExchange, authenticated)
                }
                exceptionHandling {
                    authenticationEntryPoint = ServerAuthenticationEntryPoint { exchange, _ ->
                        Mono.fromRunnable {
                            exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                            exchange.response.headers.set(HttpHeaders.WWW_AUTHENTICATE, "Bearer")
                        }
                    }
                }
                addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                httpBasic { disable() }
                formLogin { disable() }
                csrf { disable() }
            }
        }
    }
}
