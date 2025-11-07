package me.cniekirk.ontrack.core.data.mapper

import com.google.protobuf.UninitializedMessageException
import me.cniekirk.ontrack.core.domain.model.error.LocalDataError

fun Throwable.toLocalDataError(): LocalDataError {
    return when (this) {
        is UninitializedMessageException -> LocalDataError.SerializationError
        else -> LocalDataError.Unknown
    }
}