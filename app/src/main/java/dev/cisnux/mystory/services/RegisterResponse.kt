package dev.cisnux.mystory.services

import com.squareup.moshi.Json

data class RegisterResponse(

	@Json(name="error")
	val error: Boolean,

	@Json(name="message")
	val message: String
)
