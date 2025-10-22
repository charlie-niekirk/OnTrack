package me.cniekirk.ontrack.core.data.util

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.serialization.SerializationException
import me.cniekirk.ontrack.core.domain.model.error.NetworkError
import retrofit2.HttpException
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
 * Wraps a Retrofit API call that returns a Response<T> and returns a Result<T, NetworkError>.
 * Handles HTTP errors, network issues, serialization errors, and unknown errors.
 *
 * @param call The suspend function representing the Retrofit API call that returns Response<T>.
 * @return Result<T, NetworkError> containing either the successful response body or a NetworkError.
 */
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T, NetworkError> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Ok(body)
            } else {
                Err(NetworkError.HttpError(code = response.code(), message = "Response body is null"))
            }
        } else {
            Err(
                NetworkError.HttpError(
                    code = response.code(),
                    message = response.message(),
                    body = response.errorBody()?.string()
                )
            )
        }
    } catch (e: HttpException) {
        Timber.e(e)
        Err(
            NetworkError.HttpError(
                code = e.code(),
                message = e.message(),
                body = e.response()?.errorBody()?.string()
            )
        )
    } catch (e: IOException) {
        Timber.e(e)
        Err(NetworkError.NetworkFailure(e))
    } catch (e: SerializationException) {
        Timber.e(e)
        Err(NetworkError.SerializationError(e))
    } catch (e: Throwable) {
        Timber.e(e)
        Err(NetworkError.Unknown(e))
    }
}