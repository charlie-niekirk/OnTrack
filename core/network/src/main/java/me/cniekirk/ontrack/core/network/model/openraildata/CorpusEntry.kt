package me.cniekirk.ontrack.core.network.model.openraildata

import kotlinx.serialization.SerialName

data class CorpusEntry(
    @SerialName("NLCDESC") val name: String?,
    @SerialName("3ALPHA") val crs: String?,
    @SerialName("TIPLOC") val tiploc: String?
)