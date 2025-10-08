package me.cniekirk.ontrack.core.network.api.openraildata

import me.cniekirk.ontrack.core.network.model.openraildata.CorpusEntry
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenRailDataApi {

    @GET("ntrod/SupportingFileAuthenticate")
    suspend fun getCorpus(
        @Query("type") type: String = "CORPUS"
    ): List<CorpusEntry>
}