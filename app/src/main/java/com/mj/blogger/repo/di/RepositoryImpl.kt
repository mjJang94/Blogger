package com.mj.blogger.repo.di

import com.mj.blogger.repo.local.UserDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val dataStore: UserDataStore,
) : Repository {

    override suspend fun storeEmail(email: String) = dataStore.storeEmail(email)
    override val emailFlow: Flow<String> = dataStore.emailFlow

    override suspend fun storeUserId(id: String) = dataStore.storeUserId(id)
    override val userIdFlow: Flow<String> = dataStore.userIdFlow

    override suspend fun clearAll() = dataStore.clearAll()
}