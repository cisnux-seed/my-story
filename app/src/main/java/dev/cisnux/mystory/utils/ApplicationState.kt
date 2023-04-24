package dev.cisnux.mystory.utils

sealed interface ApplicationState<out T> {
    class Loading<T> : ApplicationState<T>
    class Failed<T>(val failure: Failure) : ApplicationState<T>
    class Success<T>(val data: T?) : ApplicationState<T>
}