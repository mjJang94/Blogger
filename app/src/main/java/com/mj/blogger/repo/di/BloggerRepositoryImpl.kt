package com.mj.blogger.repo.di

import com.mj.blogger.repo.local.UserDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BloggerRepositoryImpl @Inject constructor(
    private val dataStore: UserDataStore,
): BloggerRepository {

    override suspend fun storeUserId(id: String) {
        dataStore.storeUserId(id)
    }

    override val userIdFlow: Flow<String> = dataStore.userIdFlow
}