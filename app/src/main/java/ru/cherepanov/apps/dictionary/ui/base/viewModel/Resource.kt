package ru.cherepanov.apps.dictionary.ui.base.viewModel

data class Resource<T>(
    val nullableData: T? = null,
    val errorMessage: String? = null,
    private val inProgress: Boolean = false
) {
    val data: T
        get() = nullableData!!

    fun isLoading() = inProgress
    fun isError() = errorMessage != null
    fun isSuccess() = nullableData != null && !isError() && !inProgress
    fun isEmpty() = !inProgress && errorMessage == null && nullableData == null

    companion object {
        fun <T> loading(data: T? = null) = Resource(nullableData = data, inProgress = true)
        fun <T> success(data: T) = Resource(nullableData = data)
        fun <T> error(data: T? = null, error: Throwable) =
            Resource(nullableData = data, errorMessage = error.message)
    }
}

fun <T> Resource<T>.updateData(updater: (T?) -> T?): Resource<T> {
    return copy(nullableData = updater(nullableData))
}