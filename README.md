# Kotlin Spotify Web API
This is the [Kotlin](https://kotlinlang.org/) implementation of the [Spotify Web API](https://developer.spotify.com/web-api/)

## Contents
  1. **[Downloading](#downloading)**
  2. **[Creating a SpotifyAPI or SpotifyClientAPI object](#creating-a-spotifyAPI-or-spotifyClientAPI-object)**
  3. **[What is the SpotifyRestAction class?](#what-is-the-spotifyRestAction-class?)**
  4. **[Using the Library](#using-the-library)**

## Downloading
This library is available via Maven Central. 

### Maven:
```
<dependency>
    <groupId>com.adamratzman</groupId>
    <artifactId>spotify-web-api-kotlin</artifactId>
    <version>3.1.01</version>
</dependency>
```

### Gradle
```
compile group: 'com.adamratzman', name: 'spotify-web-api-kotlin', version: '3.1.01'
```

To use the latest snapshot instead, you must add the Jitpack repository
```
repositories {
	maven { url 'https://jitpack.io' }
}
```
Then, you can use the following:
```
dependencies {
	compile 'com.github.adamint:spotify-web-api-kotlin:master-SNAPSHOT'
}
```

## Creating a SpotifyAPI or SpotifyClientAPI object
In order to use the methods in this library, you must create either a `SpotifyAPI` or `SpotifyClientAPI` object using their respective exposed builders. Client-specific methods are unable to be accessed with the generic SpotifyAPI, rather you must create an instance of the Client API.

### SpotifyAPI
The SpotifyAPI `Token` automatically regenerates when needed.
To build it, you must pass the application id and secret.
```kotlin
    val api = SpotifyAPI.Builder("application id", "application secret").build()
```
*Note:* You are **unable** to use any client endpoint. 

### SpotifyClientAPI
All endpoints inside SpotifyAPI can be accessed within SpotifyClientAPI.
The SpotifyClientAPI's automatic refresh is available *only* when building with
an authorization code. Otherwise, it will expire in `Token.expires_in` seconds after creation.

You have two options when building the Client API.
1. You can use [Implicit Grant access tokens](https://developer.spotify.com/web-api/authorization-guide/#implicit_grant_flow) with 
`Builder.buildToken(token: String)`. However, this is a one-time token that cannot be refreshed.
2. You can use the [Authorization code flow](https://developer.spotify.com/web-api/authorization-guide/#authorization_code_flow). We provide a method
with `Builder.buildAuthCode(code: String, automaticRefresh: Boolean)`to generate the flow url with Builder.getAuthUrl(vararg scopes: Scope), allowing you to request specific 
scopes. This library does not provide a method to retrieve the code from your 
callback URL. You must implement that with a web server. This method allows you 
to choose whether to use automatic token refresh.

## What is the SpotifyRestAction class?
I wanted users of this library to have as much flexibility as possible. This 
includes options for asynchronous and blocking execution in all endpoints. However, 
 due to this, you **must** call one of the provided methods in order for the call 
 to execute! The `SpotifyRestAction` provides four methods for use: 1 blocking and 3 async.
- `complete()` blocks the current thread and returns the result
- `queue(consumer: (T) -> Unit)` executes the provided callback as soon as the request 
is asynchronously evaluated
- `queueAfter(quantity: Int, timeUnit: TimeUnit, consumer: (T) -> Unit)` executes the 
provided callback after the provided time. As long as supplier execution is less than the provided 
time, this will likely be accurate within a few milliseconds.
- `asFuture()` transforms the supplier into a `CompletableFuture` 

## Using the Library
### Generic Request
```kotlin
    val api = SpotifyAPI.Builder("appId", "appSecret").build()
    val trackSearch = api.search.searchTrack("Si t'étais là", market = Market.FR)
    // with optional parameter of market
    
    fun blocking() {
        val trackPaging = trackSearch.complete()
        // iterate through, see total found, etc. with the paging object..
    }
    
    fun async() {
        trackSearch.queueAfter(2, TimeUnit.SECONDS, { result ->
            // do whatever with the result
            // this will be executed 2000 += ~2 ms after invocation
        })
    }
```

### Endpoint List
#### SpotifyAPI:
   - **[AlbumAPI (SpotifyAPI.albums)](https://developer.spotify.com/web-api/album-endpoints/)**
        1. `getAlbum` returns found Album
        2. `getAlbums` returns found Albums. Use for multiple
        3. `getAlbumTracks` returns a `LinkedResult` (with href to album) of SimpleTracks
   - **[ArtistAPI (SpotifyAPI.artists)](https://developer.spotify.com/web-api/artist-endpoints/)**
        1. `getArtist` returns the found artist
        2. `getArtists` returns a list of Artist objects. Use for multiple.
        3. `getArtistAlbums` returns a `LinkedResult` of SimpleAlbums representing the Artist's albums
        4. `getArtistTopTracks` returns a List of full Track objects
        5. `getRelatedArtists` returns a List of Artist objects relating to the searched artist
   - **[BrowseAPI (SpotifyAPI.browse)]()**
        1. `getNewReleases` returns a `PagingObject` of recent Albums
        2. `getFeaturedPlaylists` returns a FeaturedPlaylists object of playlists featured by Spotify
        3. `getCategoryList` returns a `PagingObject` of official Spotify categories
        4. `getCategory` returns a singular SpotifyCategory object by id
        5. `getPlaylistsForCategory` returns a `PagingObject` of top simple playlists for the specified category id
        6. `getRecommendations` returns a RecommendationResponse. Parameters include seed artists, genres, tracks, and 
        tuneable track attributes listed [here](https://developer.spotify.com/web-api/complete-recommendations/)
   - **[PublicFollowingAPI (SpotifyAPI.publicFollowing)]()**
        1. 
   - **[PlaylistsAPI (SpotifyAPI.playlists)]()**
   - **[SearchAPI (SpotifyAPI.search)]()**
   - **[TracksAPI (SpotifyAPI.tracks)]()**
   - **[PublicUserAPI (SpotifyAPI.users)]()**
   to be continued..
