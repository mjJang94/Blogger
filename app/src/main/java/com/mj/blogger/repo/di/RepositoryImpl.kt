package com.mj.blogger.repo.di

import com.mj.blogger.repo.local.UserDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val dataStore: UserDataStore,
): Repository {

    override suspend fun storeUserId(id: String) {
        dataStore.storeUserId(id)
    }

    override val userIdFlow: Flow<String> = dataStore.userIdFlow
}