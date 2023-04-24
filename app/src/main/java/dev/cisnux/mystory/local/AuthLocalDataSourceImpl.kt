package dev.cisnux.mystory.local

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthLocalDataSourceImpl @Inject constructor(
    private val encryptedSharedPreferences: SharedPreferences
) :
    AuthLocalDataSource {
    override suspend fun saveToken(token: String) = withContext(Dispatchers.IO) {
        encryptedSharedPreferences.edit().putString(
            API_TOKEN, token
        ).apply()
    }

    override suspend fun deleteToken() = withContext(Dispatchers.IO) {
        encryptedSharedPreferences.edit().remove(API_TOKEN).apply()
    }

    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        encryptedSharedPreferences.getString(API_TOKEN, null)
    }

    companion object {
        const val API_TOKEN = "api_token"
    }
}