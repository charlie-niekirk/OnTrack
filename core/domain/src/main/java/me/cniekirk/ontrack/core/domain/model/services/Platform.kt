package me.cniekirk.ontrack.core.domain.model.services

sealed interface Platform {

    data class Confirmed(
        val platformName: String,
        val isChanged: Boolean
    ) : Platform

    data class Estimated(
        val platformName: String,
        val isChanged: Boolean
    ) : Platform
}