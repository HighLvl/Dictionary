package ru.cherepanov.apps.dictionary.domain.model

sealed interface Resource<T> {
    class Loading<T> : Resource<T>
    data class Error<T>(val throwable: Throwable) : Resource<T>
    data class Success<T>(val data: T) : Resource<T>

    companion object {
        fun <T> loading(): Resource<T> = Loading()
        fun <T> error(throwable: Throwable): Resource<T> = Error(throwable)
        fun <T> success(data: T): Resource<T> = Success(data)
    }
}