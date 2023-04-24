package dev.cisnux.mystory.services

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("register")
    suspend fun createUser(
        @Body userRegister: UserRegister
    ): RegisterResponse

    @POST("login")
    suspend fun userLogin(
        @Body userLogin: UserLogin
    ): LoginResponse
}