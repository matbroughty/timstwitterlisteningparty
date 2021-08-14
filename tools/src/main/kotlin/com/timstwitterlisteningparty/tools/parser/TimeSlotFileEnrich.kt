package com.timstwitterlisteningparty.tools.parser

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.timstwitterlisteningparty.tools.social.Album
import com.timstwitterlisteningparty.tools.social.SpotifyUtils
import com.timstwitterlisteningparty.tools.social.TweetUtils
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.FileWriter
import java.io.InputStream
import java.io.StringReader
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList


/**
 * Reads http://www.sk7software.co.uk/listeningparty/scripts/listParties.php to get the
 * replay id's and the tweeters and spotify link and enriches the "data/time-slot-data.csv" with them
 */
@Component
class TimeSlotFileEnrich {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun enrich(fileName: String = "data/time-slot-data.csv", inputStream: InputStream? = null,
             writeToFile: Boolean = false, newFileName: String = fileName): String {

    val addedReplayId = ArrayList<Int>()
    val replayMap: Map<Int, Replay> = ReplayPHPScript().readPhpReplayScript()
    replayMap.forEach { logger.debug(it.toString()) }
    val existingList = TimeSlotReader(fileName, inputStream).timeSlots
    existingList.forEach {
      if (it.spotifyImgLink.isEmpty()) {
        val album = SpotifyUtils().findAlbum(it.band, it.album)
        if (album != null) {
          logger.info("found album $album")
          it.spotifyImgLink = album.mediumImgLink
          it.spotifyImgLinkSmall = album.smallImgLink
          it.spotifyImgLinkLarge = album.largeImgLink
          it.spotifyLink = album.spotifyLink.toString()
          it.spotifyYear = album.year
        } else {
          logger.warn("Could not find album for $it")
        }
      }
      // update those without small or large image that do have a spotify medium image
      if ((it.spotifyImgLinkSmall.isEmpty() || it.spotifyImgLinkLarge.isEmpty()) && it.spotifyImgLink.contains("i.scdn.co")) {
        val albumId = it.getSpotifyAlbumId()
        logger.info("Spotify Album Id $albumId for album ${it.album} and band ${it.band}")
        val album: Album? = if (albumId.isEmpty()) {
          SpotifyUtils().findAlbum(it.band, it.album)
        } else {
          SpotifyUtils().searchByAlbumId(albumId)
        }
        if (album != null) {
          it.spotifyImgLinkSmall = album.smallImgLink
          it.spotifyImgLinkLarge = album.largeImgLink
        } else {
          logger.warn("Could not find album for $it so small and large image copied from medium image")
          it.spotifyImgLinkSmall = it.spotifyImgLink
          it.spotifyImgLinkLarge = it.spotifyImgLink
        }
      }


      if (replayMap.containsKey(it.hashBandAlbum())) {
        val replay = replayMap[it.hashBandAlbum()]
        if (replay != null) {
          // set the replay link
          it.replayLink = replay.fullReplayLink()
          // now check if we need to build a collection from it
          if (it.requiresTwitterCollection()) {
            logger.info("creating collection for replay $replay")
            it.twitterCollectionLink = TweetUtils().createCollection(replay)
            addedReplayId.add(replay.trimmedId.toInt())
          }



        }
        // get tweeters from replay page as this has actual list.  The time slot data
        // has main artist/band first which replay doesn't so keep that as first , remove duplicate
        // and remove any double separators as a result of this madness
        val replayTweeters = replayMap[it.hashBandAlbum()]?.twitterIds ?: ""
        if (it.hasReplay() && replayTweeters.isNotEmpty()) {
          it.tweeters = "${it.tweeterList().first()}:${replayTweeters.replace(it.tweeterList().first(), "")}".replace("::",":")
        }

        // spotify link from php if
        if (it.spotifyLink.isEmpty()) {
          it.spotifyLink = replayMap[it.hashBandAlbum()]?.spotifyLink ?: ""
        }

      }

    }


    val counter = AtomicInteger(0)
    existingList.sortedBy { it.isoDate }.forEach { addListeningPartyNumber(it, counter) }

    if (writeToFile) {
      val fileWriter = FileWriter(newFileName)
      val sbc = StatefulBeanToCsvBuilder<TimeSlot>(fileWriter)
        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
        .build()
      sbc.write(existingList.sortedBy { it.isoDate })
      fileWriter.close()
    }

    // return it as a string by writing again
    val writer = StringWriter()
    val sbc = StatefulBeanToCsvBuilder<TimeSlot>(writer)
      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
      .build()
    sbc.write(existingList.sortedBy { it.isoDate })
    writer.close()

    // add any new replay id's to the collections.  Hardcoded collection-ids as the first-tweet.html uses them
    if (addedReplayId.isNotEmpty()) {
      TweetUtils().ttlpFirstTweetCollection(collectionIdStr = "custom-1268856689010376706", replayIdStr = addedReplayId.min()!!.toString())
      // specify order but not used as we already have the collection
      TweetUtils().ttlpFirstTweetCollection(collectionIdStr = "custom-1268856779288588289", replayIdStr = addedReplayId.min()!!.toString(), order = "tweet_reverse_chron")
    }

    return writer.toString()
  }

  override fun equals(other: Any?): Boolean {
    return super.equals(other)
  }

  private fun addListeningPartyNumber(it: TimeSlot, counter: AtomicInteger) {
    // Not scheduled yet
    if (it.is1970() || it.band.contains("Gorilla", ignoreCase = true) || it.band.contains("No Listening Party", ignoreCase = true)
      || it.replayLink.contains("no-replay", ignoreCase = true)
    ) {
      it.listeningPartyNumber = "-1"
    } else {
      it.listeningPartyNumber = counter.incrementAndGet().toString()
    }
    logger.debug("Time Slot is $it")

  }

}


class ReplayPHPScript {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * Reads the slightly weirdly formed listParties.php so uses jsoup and then gets rid of markup and finally parses as csv
   * @return Map<Int, Replay> - a hash of band name/album to replay object
   */
  fun readPhpReplayScript(fileName: String = "http://www.sk7software.co.uk/listeningparty/replay/live/scripts/listParties.php"): Map<Int, Replay> {
    // slightly weirdly formed so use jsoup and then get rid of markup and parse as csv
    val stockURL = Jsoup.connect(fileName).get()
    val replayIds = stockURL.select("body").toString().replace("<body>\n", "").replace("</body>", "").replace("<br>", "")
    logger.debug("replay ids $replayIds")
    val builder = CsvToBeanBuilder<Replay>(StringReader(replayIds))
    val idList: List<Replay> = builder.withType(Replay::class.java).withIgnoreEmptyLine(true).withSkipLines(1).build().parse()
    //idList.forEach { logger.info("tweeted ${tweetUtils.tweet("Replay available:  ${it.band} : ${it.album} at ${fullReplayLink(it.trimmedId)} #TimsTwitterListeningParty")}") }
    idList.forEach { logger.debug(it.toString()) }
    return idList.stream().filter { it.isEmpty().not() }
      .toList()
      .map { it.hashBandAlbum() to it }
      .toMap()
  }

}


