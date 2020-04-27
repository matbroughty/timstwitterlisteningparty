package com.timstwitterlisteningparty.tools.parser

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.*
import kotlin.streams.toList

@Component
class TimeSlotFileReplayLink {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun addReplayLink(fileName: String = "data/time-slot-data.csv", inputStream: InputStream? = null,
                    writeToFile: Boolean = false, newFileName: String = fileName): String {

    logger.info("Parsing URL from '{}'", "https://timstwitterlisteningparty.com/snippets/replay/feed_index_snippet.html")
    val doc: Document = Jsoup.connect("https://timstwitterlisteningparty.com/snippets/replay/feed_index_snippet.html").get()
    val replayMap: Map<Int, TimeSlot> = doc.select("tr")
      .stream()
      .filter { it.children().select("div#album-div").isEmpty() } // don't want the random album titles
      .map { it.buildCsvRow() }
      .toList()
      .map { it.hashBandAlbum() to it }.toMap()

    replayMap.forEach { logger.info(it.toString()) }

    val csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> =
      if (inputStream != null) CsvToBeanBuilder<TimeSlot>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<TimeSlot>(FileReader(fileName))
      }

    val existingList: List<TimeSlot> = csvToBeanBuilder.withType(TimeSlot::class.java).withIgnoreEmptyLine(true).build().parse()
    existingList.forEach {
      if (replayMap.containsKey(it.hashBandAlbum())) {
        it.replayLink = fullReplayLink(replayMap[it.hashBandAlbum()]?.replayLink ?: "")
      }
    }
    existingList.forEach { logger.info(it.toString()) }

    if(writeToFile) {
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

  private fun fullReplayLink(id: String) : String{
    if(id.isBlank()){
      return ""
    }
    return "https://timstwitterlisteningparty.com/pages/replay/feed_$id.html"
  }

  private fun Element?.buildCsvRow(): TimeSlot {
    if (this != null) {
      logger.debug("row {}", html())
      return TimeSlot(
        replayLink = StringUtils.substringAfter(StringUtils.substringBefore(select("img")[0].attr("src").toString(), "_small"),"feed_"),
        album = (select("td")[1].childNode(3) as TextNode).text(),
        band = (select("td")[1].childNode(1) as Element).text()
      )
    }
    return TimeSlot()
  }

  }

