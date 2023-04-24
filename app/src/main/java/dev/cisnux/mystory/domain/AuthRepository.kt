package dev.cisnux.mystory.domain

import dev.cisnux.mystory.utils.ApplicationState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun isAlreadyLogin(): String?
    fun login(userAuth: UserAuth): Flow<ApplicationState<String>>
    fun register(userAuth: UserAuth): Flow<ApplicationState<String>>
    suspend fun logout()
}