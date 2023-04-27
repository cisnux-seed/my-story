package dev.cisnux.mystory.domain

import dev.cisnux.mystory.services.UserLogin
import dev.cisnux.mystory.services.UserRegister

data class UserAuth(
    val email: String,
    val password: String
) {
    private var _username: String = ""
    val username get() = _username

    constructor(username: String, email: String, password: String) : this(email, password) {
        this._username = username
    }
}

fun UserAuth.asUserLogin(): UserLogin = UserLogin(
    email,
    password,
)

fun UserAuth.asUserRegister(): UserRegister = UserRegister(
    username,
    email,
    password
)