package dev.cisnux.mystory.services

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserRegister(
    @Json(name = "name") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)
