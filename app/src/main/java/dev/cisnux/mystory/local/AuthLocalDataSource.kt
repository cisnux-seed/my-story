package dev.cisnux.mystory.local

interface AuthLocalDataSource {
    suspend fun saveToken(token: String)
    suspend fun deleteToken()
    suspend fun getToken(): String?
}