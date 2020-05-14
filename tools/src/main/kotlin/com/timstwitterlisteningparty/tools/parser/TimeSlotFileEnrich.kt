package com.timstwitterlisteningparty.tools.parser

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.timstwitterlisteningparty.tools.social.SpotifyUtils
import com.timstwitterlisteningparty.tools.social.TweetUtils
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.FileWriter
import java.io.InputStream
import java.io.StringReader
import java.io.StringWriter
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

    val replayMap: Map<Int, Replay> = ReplayPHPScript().readPhpReplayScript()
    replayMap.forEach { logger.debug(it.toString()) }
    val existingList = TimeSlotReader(fileName, inputStream).timeSlots
    existingList.forEach {
      if(it.spotifyImgLink.isEmpty()){
        val album = SpotifyUtils().findAlbum(it.band, it.album)
        if(album != null){
          logger.info("found album $album")
          it.spotifyImgLink = album.imgLink
          it.spotifyImgLinkSmall = album.smallImgLink
          it.spotifyLink = album.spotifyLink.toString()
        }else{
          logger.warn("Could not find album for $it")
        }
      }
      // update those without small image
      if(it.spotifyImgLinkSmall.isEmpty()){
        val album = SpotifyUtils().findAlbum(it.band, it.album)
        if(album != null) {
          it.spotifyImgLinkSmall = album.smallImgLink
        }else{
          logger.warn("Could not find album for $it so no small image")
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
          }

        }
        // only set the tweeters if the time slot data was empty
        if (it.tweeters.isEmpty()) {
          it.tweeters = replayMap[it.hashBandAlbum()]?.twitterIds ?: ""
        }

        // spotify link from php if
        if(it.spotifyLink.isEmpty()){
          it.spotifyLink = replayMap[it.hashBandAlbum()]?.spotifyLink ?: ""
        }

      }
    }
    existingList.forEach { logger.debug(it.toString()) }

    if (writeToFile) {
      val fileWriter = FileWriter(newFileName)
      val sbc = StatefulBeanToCsvBuilder<TimeSlot>(fileWriter)
        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
        .build()
      sbc.write(existingList.sortedBy{it.isoDate})
      fileWriter.close()
    }

    // return it as a string by writing again
    val writer = StringWriter()
    val sbc = StatefulBeanToCsvBuilder<TimeSlot>(writer)
      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
      .build()
    sbc.write(existingList.sortedBy{it.isoDate})
    writer.close()
    return writer.toString()
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


