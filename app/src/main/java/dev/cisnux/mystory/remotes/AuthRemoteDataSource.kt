package dev.cisnux.mystory.remotes

import dev.cisnux.mystory.services.LoginResponse
import dev.cisnux.mystory.services.RegisterResponse
import dev.cisnux.mystory.services.UserLogin
import dev.cisnux.mystory.services.UserRegister

interface AuthRemoteDataSource {
    suspend fun register(userRegister: UserRegister): RegisterResponse
    suspend fun login(userLogin: UserLogin): LoginResponse
}