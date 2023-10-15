package com.mj.blogger.repo.di

import kotlinx.coroutines.flow.Flow

interface BloggerRepository {
    suspend fun storeUserId(id: String)
    val userIdFlow: Flow<String>
}