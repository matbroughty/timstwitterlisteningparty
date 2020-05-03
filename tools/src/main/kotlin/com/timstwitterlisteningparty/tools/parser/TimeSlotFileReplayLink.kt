package com.timstwitterlisteningparty.tools.parser

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.timstwitterlisteningparty.tools.twitter.TweetUtils
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.*
import kotlin.streams.toList


/**
 * Reads http://www.sk7software.co.uk/listeningparty/scripts/listParties.php to get the
 * replay id's and the tweeters and enriches the "data/time-slot-data.csv" with them
 * as column 5 and 6
 */
@Component
class TimeSlotFileReplayLink(val tweetUtils: TweetUtils) {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun addReplayLink(fileName: String = "data/time-slot-data.csv", inputStream: InputStream? = null,
                    writeToFile: Boolean = false, newFileName: String = fileName): String {

    val replayMap: Map<Int, Replay> = ReplayPHPScript().readPhpReplayScript()
    replayMap.forEach { logger.info(it.toString()) }
    val csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> =
      if (inputStream != null) CsvToBeanBuilder<TimeSlot>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<TimeSlot>(FileReader(fileName))
      }
    val existingList: List<TimeSlot> = csvToBeanBuilder.withType(TimeSlot::class.java).withIgnoreEmptyLine(true).build().parse()
    existingList.forEach {
      if (replayMap.containsKey(it.hashBandAlbum())) {
        val replay = replayMap[it.hashBandAlbum()]
        if (replay != null) {
          if(it.requiresTwitterCollection() && Integer.valueOf(replay.trimmedId) > 80){
            logger.info("creating collection for replay $replay")
            it.twitterCollectionLink = TweetUtils().createCollection(replay)
          }
          // set it
          it.replayLink = replay.fullReplayLink()
        }
        // only set the tweeters if the time slot data was empty
        if (it.tweeters.isEmpty()) {
          it.tweeters = replayMap[it.hashBandAlbum()]?.twitterIds ?: ""
        }
      }
    }
    existingList.forEach { logger.info(it.toString()) }

    if (writeToFile) {
      val fileWriter = FileWriter(newFileName)
      val sbc = StatefulBeanToCsvBuilder<TimeSlot>(fileWriter)
        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
        .build()
      sbc.write(existingList)
      fileWriter.close()
    }

    val writer = StringWriter()
    val sbc = StatefulBeanToCsvBuilder<TimeSlot>(writer)
      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
      .build()
    sbc.write(existingList)
    writer.close()
    return writer.toString()

  }

}


class ReplayPHPScript {

  private val logger = LoggerFactory.getLogger(javaClass)

  /**
   * Reads the slightly weirdly formed listParties.php so uses jsoup and then gets rid of markup and finall parses as csv
   * @return Map<Int, Replay> - a hash of band name/album to replay object
   */
  fun readPhpReplayScript(fileName: String = "http://www.sk7software.co.uk/listeningparty/scripts/listParties.php"): Map<Int, Replay> {
    // slightly weirdly formed so use jsoup and then get rid of markup and parse as csv
    val stockURL = Jsoup.connect(fileName).get()
    val replayIds = stockURL.select("body").toString().replace("<body>\n", "").replace("</body>", "").replace("<br>", "")
    logger.info("replay ids $replayIds")
    val builder = CsvToBeanBuilder<Replay>(StringReader(replayIds))
    val idList: List<Replay> = builder.withType(Replay::class.java).withIgnoreEmptyLine(true).withSkipLines(1).build().parse()
    //idList.forEach { logger.info("tweeted ${tweetUtils.tweet("Replay available:  ${it.band} : ${it.album} at ${fullReplayLink(it.trimmedId)} #TimsTwitterListeningParty")}") }
    idList.forEach { logger.info(it.toString()) }
    return idList.stream().filter { it.isEmpty().not() }
      .toList()
      .map { it.hashBandAlbum() to it }
      .toMap()
  }

}


class ReplayFeed {

  private val logger = LoggerFactory.getLogger(javaClass)


}

