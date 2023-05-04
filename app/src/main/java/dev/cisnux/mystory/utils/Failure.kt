package dev.cisnux.mystory.utils

sealed class Failure(override var message: String?) : Exception(message) {
    class ConnectionFailure(
        message: String? = null,
    ) : Failure(message)

    class NotFoundFailure(
        message: String? = null,
    ) : Failure(message)

    class ServerFailure(
        message: String? = null,
    ) : Failure(message)

    class BadRequestFailure(
        message: String? = null,
    ) : Failure(message)

    class AuthenticationFailure(
        message: String? = null,
    ) : Failure(message)
}

val HTTP_FAILURES = mapOf(
    400 to Failure.BadRequestFailure(),
    401 to Failure.AuthenticationFailure(),
    404 to Failure.NotFoundFailure(),
    500 to Failure.ServerFailure()
)