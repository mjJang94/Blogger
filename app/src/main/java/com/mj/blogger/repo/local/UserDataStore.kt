package com.mj.blogger.repo.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserDataStore(context: Context) {

    companion object {
        private val USER_ID = stringPreferencesKey("USER_ID")
    }

    private val userDataStore = context.dataStore

    suspend fun storeUserId(id: String){
        userDataStore.edit { store ->
            store[USER_ID] = id
        }
    }

    var userIdFlow: Flow<String> = userDataStore.data.map { preferences ->
        preferences[USER_ID] ?: ""
    }
}