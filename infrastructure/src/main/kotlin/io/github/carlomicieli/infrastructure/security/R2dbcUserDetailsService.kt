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

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository

interface UsersService {
    suspend fun findByUsername(username: String): UserDetails?
    suspend fun registerUser(username: String, password: String, roles: List<String> = listOf()): UserDetails
}

class R2dbcUserDetailsService(private val usersRepository: CoroutineUsersRepository, private val passwordEncoder: PasswordEncoder) : UsersService {
    override suspend fun findByUsername(username: String): UserDetails? {
        val result = usersRepository.findByUsername(username)
        return if (result != null) {
            CustomUserDetails(result)
        } else {
            null
        }
    }

    override suspend fun registerUser(username: String, password: String, roles: List<String>): UserDetails {
        val user = User(
            username,
            password = passwordEncoder.encode(password),
            expired = false,
            locked = false,
            role = roles.firstOrNull() ?: "USER",
            version = 0
        )
        usersRepository.save(user)
        return CustomUserDetails(user)
    }
}

@Repository
interface CoroutineUsersRepository : CoroutineCrudRepository<User, String> {
    suspend fun findByUsername(username: String): User?
}

@Table("users")
data class User(
    @Id
    val username: String,
    val password: String,

    @Column("is_expired")
    val expired: Boolean = false,

    @Column("is_locked")
    val locked: Boolean = false,

    val role: String = "USER",

    @Version
    val version: Int = 0
)

class CustomUserDetails(private val user: User) : UserDetails {
    override fun getUsername() = user.username
    override fun getPassword() = user.password
    override fun isEnabled() = !user.expired
    override fun isCredentialsNonExpired() = !user.expired
    override fun isAccountNonExpired() = !user.expired
    override fun isAccountNonLocked() = !user.locked
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority(user.role))
}
