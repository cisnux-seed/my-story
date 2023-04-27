package dev.cisnux.mystory.repositories

import dev.cisnux.mystory.domain.AuthRepository
import dev.cisnux.mystory.domain.UserAuth
import dev.cisnux.mystory.domain.asUserLogin
import dev.cisnux.mystory.domain.asUserRegister
import dev.cisnux.mystory.local.AuthLocalDataSource
import dev.cisnux.mystory.remote.AuthRemoteDataSource
import dev.cisnux.mystory.utils.ApplicationState
import dev.cisnux.mystory.utils.Failure.ConnectionFailure
import dev.cisnux.mystory.utils.HTTP_FAILURES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {
    override suspend fun isAlreadyLogin(): String? = localDataSource.getToken()

    override suspend fun logout() = localDataSource.deleteToken()

    override fun login(userAuth: UserAuth): Flow<ApplicationState<String>> =
        flow {
            try {
                emit(ApplicationState.Loading())
                val response = remoteDataSource.login(userAuth.asUserLogin())
                localDataSource.saveToken(response.loginResult.token)
                emit(ApplicationState.Success(response.loginResult.token))
            } catch (e: IOException) {
                e.printStackTrace()
                emit(ApplicationState.Failed(ConnectionFailure()))
            } catch (e: HttpException) {
                val statusCode = e.response()?.code()
                val failure = HTTP_FAILURES[statusCode]
                val errorBody = e.response()?.errorBody()?.string()
                errorBody?.let {
                    failure?.message = JSONObject(it).getString("message")
                }
                failure?.let {
                    emit(ApplicationState.Failed(it))
                }
            }
        }

    override fun register(
        userAuth: UserAuth
    ): Flow<ApplicationState<String>> =
        flow {
            try {
                emit(ApplicationState.Loading())
                val response = remoteDataSource.register(
                    userAuth.asUserRegister()
                )
                emit(ApplicationState.Success(response.message))
            } catch (e: IOException) {
                emit(ApplicationState.Failed(ConnectionFailure()))
            } catch (e: HttpException) {
                val statusCode = e.response()?.code()
                val failure = HTTP_FAILURES[statusCode]
                val errorBody = e.response()?.errorBody()?.string()
                errorBody?.let {
                    failure?.message = JSONObject(it).getString("message")
                }
                failure?.let {
                    emit(ApplicationState.Failed(it))
                }
            }
        }

}