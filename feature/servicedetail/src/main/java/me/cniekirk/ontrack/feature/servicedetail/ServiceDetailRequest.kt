package me.cniekirk.ontrack.feature.servicedetail

data class ServiceDetailRequest(
    val serviceUid: String,
    val year: String,
    val month: String,
    val day: String
)
