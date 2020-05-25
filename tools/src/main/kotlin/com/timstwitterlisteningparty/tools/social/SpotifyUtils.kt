package com.timstwitterlisteningparty.tools.social

import com.neovisionaries.i18n.CountryCode
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.enums.ModelObjectType
import com.wrapper.spotify.exceptions.SpotifyWebApiException
import com.wrapper.spotify.model_objects.credentials.ClientCredentials
import com.wrapper.spotify.model_objects.specification.AlbumSimplified
import com.wrapper.spotify.model_objects.specification.Paging
import org.apache.hc.core5.http.ParseException
import org.slf4j.LoggerFactory
import java.io.IOException


class SpotifyUtils {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * Tries a search on album and artist, then all albums by artist, then all albums by name
   */
  fun findAlbum(artist: String, albumStr: String, spotifyApiParam: SpotifyApi? = null): Album? {
    try {
      val spotifyApi = spotifyApiParam ?: getSpotify()
      if (albumStr == "?") {
        logger.info("Album not decided yet so spotify search not done for $artist")
        return null
      }
      var album = search(artist, albumStr, spotifyApi)
      if(album == null) {
        // try for all of the artists albums
        var albumRequest = spotifyApi.searchAlbums(artist.trim()).limit(50).build()
        var albums = albumRequest.execute()
        logger.info("Search ${buildQuery(artist, albumStr)} didn't find anything, searching through artist albums")
        album = scrollAlbums( albums, albumStr)

        // no luck - try on album name
        if (album == null) {
          logger.info("couldn't find $albumStr by band $artist trying by band name")
          // try on album
          albumRequest = spotifyApi.searchAlbums(albumStr.trim()).limit(50).build()
          albums = albumRequest.execute()
          album = scrollAlbums(albums, albumStr)
          logger.info("Found $album by  $albumStr by band $artist ")
        }
      }
      return album
    } catch (e: IOException) {
      logger.warn("Error:  ${e.localizedMessage}", e)
    } catch (e: SpotifyWebApiException) {
      logger.warn("Error:  ${e.localizedMessage}", e)
    } catch (e: ParseException) {
      logger.warn("Error:  ${e.localizedMessage}", e)
    }

    logger.warn("didn't find $albumStr for $artist")
    return null
  }

  private fun scrollAlbums(albums: Paging<AlbumSimplified>?, album: String): Album? {
    albums?.items?.forEach { it ->
      logger.debug("album is ${it.name}")
      if (it.name.equals(album, ignoreCase = true)) {
        logger.debug("found album $album - returning album ${it.externalUrls.externalUrls["spotify"]}")
        // got it
        return it.toAlbum()
      }
    }
    return null
  }


  fun search(artist: String, album: String, spotifyApiParam: SpotifyApi? = null): Album? {
    try {
      val spotifyApi = spotifyApiParam ?: getSpotify()
      logger.info("Searching on album $album and band $artist")
      logger.debug("access token is ${spotifyApi.accessToken}")

      val q = buildQuery(artist, album)
      logger.info("query is $q")
      val search = spotifyApi.searchItem(q, ModelObjectType.ALBUM.type).market(CountryCode.GB).offset(0).limit(50).build()
      val searchRequest = search.execute()
      searchRequest.albums.items.forEach { it ->
        logger.debug("album is ${it.name}")
        if (it.name.equals(album, ignoreCase = true) || it.name.contains(album.substringBefore(" ")) || it.name.contains(album.substringBefore(" "))) {
          logger.debug("found artist $artist - returning album ${it.href}")
          // got it
          return it.toAlbum()
        }
      }
    } catch (e: IOException) {
      logger.warn("Error:  ${e.localizedMessage}", e)
    } catch (e: SpotifyWebApiException) {
      logger.warn("Error:  ${e.localizedMessage}", e)
    } catch (e: ParseException) {
      logger.warn("Error:  ${e.localizedMessage}", e)
    }

    logger.warn("didn't find $album for $artist")
    return null
  }

  private fun buildQuery(artist: String, album: String): String {
    return "album:${album.trim()} artist:${artist.trim()}"
  }

  private fun getSpotify(): SpotifyApi {

    val clientId: String? = System.getenv("spotify_client_id")
    val clientSecret: String? = System.getenv("spotify_client_secret")
    val spotifyApi = SpotifyApi.Builder()
      .setClientId(clientId)
      .setClientSecret(clientSecret)
      .build()
    val clientCredentialsRequest = spotifyApi.clientCredentials()
      .build()
    val clientCredentials: ClientCredentials = clientCredentialsRequest.execute()
    // Set access token for further "spotifyApi" object usage
    spotifyApi.accessToken = clientCredentials.accessToken
    logger.debug("Expires in: ${clientCredentials.expiresIn}")
    return spotifyApi
  }


}

fun AlbumSimplified.toAlbum(): Album {
  return Album(spotifyLink = externalUrls.externalUrls["spotify"], imgLink = images[1].url, smallImgLink = images[2].url, spotifyId = id, releaseDate = releaseDate, albumName = name, year = releaseDate)
}

data class Album(val spotifyLink: String?, val imgLink: String, val smallImgLink: String, val spotifyId: String, val releaseDate: String, val albumName: String, val year:String)

