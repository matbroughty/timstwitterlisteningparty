package com.timstwitterlisteningparty.tools.parser

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.timstwitterlisteningparty.tools.twitter.TweetUtils
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
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

    // slightly weirdly formed so use jsoup and then get rid of markup and parse as csv
    val stockURL = Jsoup.connect("http://www.sk7software.co.uk/listeningparty/scripts/listParties.php").get()
    val replayIds = stockURL.select("body").toString().replace("<body>\n", "").replace("</body>", "").replace("<br>", "")
    logger.info("replay ids $replayIds")
    val builder = CsvToBeanBuilder<Replay>(StringReader(replayIds))
    val idList: List<Replay> = builder.withType(Replay::class.java).withIgnoreEmptyLine(true).withSkipLines(1).build().parse()
    idList.forEach { logger.info(it.toString()) }
    val replayMap: Map<Int, Replay> = idList.stream().filter { it.isEmpty().not() }.toList().map { it.hashBandAlbum() to it }.toMap()

    replayMap.forEach { logger.info(it.toString()) }

    val csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> =
      if (inputStream != null) CsvToBeanBuilder<TimeSlot>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<TimeSlot>(FileReader(fileName))
      }

    val existingList: List<TimeSlot> = csvToBeanBuilder.withType(TimeSlot::class.java).withIgnoreEmptyLine(true).build().parse()
    existingList.forEach {
      if (replayMap.containsKey(it.hashBandAlbum())) {
        it.replayLink = fullReplayLink(replayMap[it.hashBandAlbum()]?.trimmedId ?: "")
        // only set the tweeters if the
        if(it.tweeters.isEmpty()) {
          it.tweeters = replayMap[it.hashBandAlbum()]?.twitterIds ?: ""
        }
        if(it.requiresTwitterCollection()){
          it.twitterCollectionLink = tweetUtils.createCollection(replayMap[it.hashBandAlbum()]?.trimmedId ?: "")
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

  private fun fullReplayLink(id: String): String {
    if (id.isBlank()) {
      return ""
    }
    return "https://timstwitterlisteningparty.com/pages/replay/feed_$id.html"
  }

  private fun Element?.buildCsvRow(): TimeSlot {
    if (this != null) {
      logger.debug("row {}", html())
      return TimeSlot(
        replayLink = StringUtils.substringAfter(StringUtils.substringBefore(select("img")[0].attr("src").toString(), "_small"), "feed_"),
        album = (select("td")[1].childNode(3) as TextNode).text(),
        band = (select("td")[1].childNode(1) as Element).text()
      )
    }
    return TimeSlot()
  }

}

