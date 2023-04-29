package dev.cisnux.mystory.remote

import dev.cisnux.mystory.services.AuthService
import dev.cisnux.mystory.services.LoginResponse
import dev.cisnux.mystory.services.RegisterResponse
import dev.cisnux.mystory.services.UserLogin
import dev.cisnux.mystory.services.UserRegister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(private val service: AuthService) :
    AuthRemoteDataSource {
    override suspend fun register(userRegister: UserRegister): RegisterResponse =
        withContext(Dispatchers.IO) {
            service.createUser(userRegister)
        }

    override suspend fun login(userLogin: UserLogin): LoginResponse = withContext(Dispatchers.IO) {
        service.userLogin(userLogin)
    }
}