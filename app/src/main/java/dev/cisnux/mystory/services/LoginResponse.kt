package dev.cisnux.mystory.services

import com.squareup.moshi.Json

data class LoginResponse(

	@Json(name="loginResult")
	val loginResult: LoginResult,

	@Json(name="error")
	val error: Boolean,

	@Json(name="message")
	val message: String
)

data class LoginResult(

	@Json(name="name")
	val name: String,

	@Json(name="userId")
	val userId: String,

	@Json(name="token")
	val token: String
)
