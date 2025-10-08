package me.cniekirk.ontrack.core.domain.model.error

sealed interface NetworkError {
    /**
     * Represents HTTP errors (non-2xx response codes) from Retrofit.
     */
    data class HttpError(
        val code: Int,
        val message: String?,
        val body: String? = null // Optional: response body for more details
    ) : NetworkError

    /**
     * Represents network-related IO exceptions, such as no connectivity or timeouts.
     */
    data class NetworkFailure(
        val throwable: Throwable
    ) : NetworkError

    /**
     * Represents serialization/deserialization errors, such as JSON parsing issues.
     */
    data class SerializationError(
        val throwable: Throwable
    ) : NetworkError

    /**
     * Represents any other unknown or unexpected errors.
     */
    data class Unknown(
        val throwable: Throwable
    ) : NetworkError
}