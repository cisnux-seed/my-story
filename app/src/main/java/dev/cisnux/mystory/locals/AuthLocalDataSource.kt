package dev.cisnux.mystory.locals

interface AuthLocalDataSource {
    suspend fun saveToken(token: String)
    suspend fun deleteToken()
    suspend fun getToken(): String?
}