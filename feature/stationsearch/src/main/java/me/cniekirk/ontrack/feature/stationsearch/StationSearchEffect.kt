package me.cniekirk.ontrack.feature.stationsearch

import androidx.annotation.StringRes

internal sealed interface StationSearchEffect {

    data class ShowError(@param:StringRes val message: Int) : StationSearchEffect
}