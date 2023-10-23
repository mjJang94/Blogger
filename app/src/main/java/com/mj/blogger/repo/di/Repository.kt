package com.mj.blogger.repo.di

import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun storeEmail(email: String)
    val emailFlow: Flow<String>

    suspend fun storeUserId(id: String)
    suspend fun userId(): String
    val userIdFlow: Flow<String>
}