/* Spotify Web API, Kotlin Wrapper; MIT License, 2017-2021; Original author: Adam Ratzman */
package com.adamratzman.spotify.endpoints.client

import com.adamratzman.spotify.GenericSpotifyApi
import com.adamratzman.spotify.SpotifyException.BadRequestException
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.endpoints.public.EpisodeApi
import com.adamratzman.spotify.http.encodeUrl
import com.adamratzman.spotify.models.Episode
import com.adamratzman.spotify.models.EpisodeList
import com.adamratzman.spotify.models.EpisodeUri
import com.adamratzman.spotify.models.serialization.toObject
import com.adamratzman.spotify.utils.Market
import com.adamratzman.spotify.utils.catch

/**
 * Endpoints for retrieving information about one or more episodes from the Spotify catalog.
 *
 * **[Api Reference](https://developer.spotify.com/documentation/web-api/reference/episodes/)**
 */
public class ClientEpisodeApi(api: GenericSpotifyApi) : EpisodeApi(api) {
    /**
     * Get Spotify catalog information for a single episode identified by its unique Spotify ID. The [Market] associated with
     * the user account will be used.
     *
     * **Reading the user’s resume points on episode objects requires the [SpotifyScope.USER_READ_PLAYBACK_POSITION] scope**
     *
     * **[Api Reference](https://developer.spotify.com/documentation/web-api/reference/episodes/get-an-episode/)**
     *
     * @param id The Spotify ID for the episode.
     *
     * @return possibly-null [Episode].
     */
    public suspend fun getEpisode(id: String): Episode? {
        return catch {
            get(
                endpointBuilder("/episodes/${EpisodeUri(id).id.encodeUrl()}").toString()
            ).toObject(Episode.serializer(), api, json)
        }
    }

    /**
     * Get Spotify catalog information for multiple episodes based on their Spotify IDs. The [Market] associated with
     * the user account will be used.
     *
     * **Invalid episode ids will result in a [BadRequestException]
     *
     * **Reading the user’s resume points on episode objects requires the [SpotifyScope.USER_READ_PLAYBACK_POSITION] scope**
     *
     * **[Api Reference](https://developer.spotify.com/documentation/web-api/reference/episodes/get-several-episodes/)**
     *
     * @param ids The id or uri for the episodes. Maximum **50**.
     *
     * @return List of possibly-null [Episode] objects.
     * @throws BadRequestException If any invalid show id is provided
     */
    public suspend fun getEpisodes(vararg ids: String): List<Episode?> {
        checkBulkRequesting(50, ids.size)
        return bulkRequest(50, ids.toList()) { chunk ->
            get(
                endpointBuilder("/episodes")
                    .with("ids", chunk.joinToString(",") { EpisodeUri(it).id.encodeUrl() })
                    .toString()
            ).toObject(EpisodeList.serializer(), api, json).episodes
        }.flatten()
    }
}
