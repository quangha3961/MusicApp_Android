package com.example.musicapp.data.datasource

import com.example.musicapp.data.model.Song
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.google.gson.JsonElement
import com.google.gson.JsonObject

// Data class cho response Audius
private data class AudiusTrackResponse(val data: List<AudiusTrack>)
private data class AudiusTrack(
    val id: String,
    val title: String,
    val user: AudiusUser,
    val artwork: JsonElement?
)
private data class AudiusUser(
    val name: String?,
    val handle: String?
)

private interface AudiusApi {
    @GET("v1/tracks/trending")
    suspend fun getTrendingTracks(@Query("app_name") appName: String): AudiusTrackResponse
}

class RemoteSongDataSource : SongDataSource {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.audius.co/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(AudiusApi::class.java)

    override fun getSongs(): List<Song> = runBlocking {
        try {
            val response = api.getTrendingTracks("myapp")
            response.data.map { track ->
                val imageUrl = track.artwork?.let { art ->
                    if (art.isJsonObject) {
                        val obj = art.asJsonObject
                        obj.get("480x480")?.asString ?: obj.get("150x150")?.asString
                    } else null
                }
                Song(
                    id = track.id,
                    title = track.title,
                    artist = track.user.name ?: track.user.handle ?: "Unknown",
                    url = "https://api.audius.co/v1/tracks/${track.id}/stream?app_name=myapp",
                    imageUrl = imageUrl
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
} 