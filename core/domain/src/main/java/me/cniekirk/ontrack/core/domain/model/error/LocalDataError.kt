package me.cniekirk.ontrack.core.domain.model.error

sealed interface LocalDataError {

    data object SerializationError : LocalDataError

    data object Unknown : LocalDataError
}