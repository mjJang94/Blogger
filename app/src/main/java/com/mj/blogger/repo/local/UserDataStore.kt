package com.mj.blogger.repo.local

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserDataStore(context: Context) {

    companion object {
        private val EMAIL = stringPreferencesKey("EMAIL")
        private val USER_ID = stringPreferencesKey("USER_ID")
    }

    private val userDataStore = context.dataStore

    suspend fun storeEmail(email: String) {
        userDataStore.edit { store ->
            store[EMAIL] = email
        }
    }

    var emailFlow: Flow<String> = userDataStore.data.map { preferences ->
        preferences[EMAIL] ?: ""
    }

    suspend fun storeUserId(id: String) {
        userDataStore.edit { store ->
            store[USER_ID] = id
        }
    }

    var userIdFlow: Flow<String> = userDataStore.data.map { preferences ->
        preferences[USER_ID] ?: ""
    }

    suspend fun clearAll() {
        userDataStore.edit { it.clear() }
    }
}