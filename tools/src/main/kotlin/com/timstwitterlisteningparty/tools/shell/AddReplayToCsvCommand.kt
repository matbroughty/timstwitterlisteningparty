package com.timstwitterlisteningparty.tools.shell

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvToBeanBuilder
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.timstwitterlisteningparty.tools.parser.RecordStore
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.slf4j.LoggerFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import java.io.FileReader
import java.io.FileWriter
import kotlin.streams.toList

@ShellComponent
class AddReplayToCsvCommand {
  private val logger = LoggerFactory.getLogger(javaClass)

  @ShellMethod("Add a column to the generated-time-slot.date.csv file from the url view-source: https://timstwitterlisteningparty.com/snippets/replay/feed_index_snippet.html")
  fun replay(): String {
    return "The generated-time-slot.date.csv file was created: ${updateFile()}"
  }


  fun updateFile() {

    logger.info("Parsing URL from '{}'", "https://timstwitterlisteningparty.com/snippets/replay/feed_index_snippet.html")
    val doc: Document = Jsoup.connect("https://timstwitterlisteningparty.com/snippets/replay/feed_index_snippet.html").get()
    val replayMap : Map<Int, TimeSlot> = doc.select("tr")
      .stream()
      .map { it.buildCsvRow() }
      .toList()
      .map{ it.hashBandAlbum() to it }.toMap()

    replayMap.forEach { logger.info(it.toString())}

    val csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> = CsvToBeanBuilder<TimeSlot>(FileReader("data/time-slot-data.csv"))
    val existingList: List<TimeSlot> = csvToBeanBuilder.withType(TimeSlot::class.java).withIgnoreEmptyLine(true).build().parse()
    existingList.forEach {
      if(replayMap.containsKey(it.hashBandAlbum())){
        it.replayLink = fullReplayLink(replayMap[it.hashBandAlbum()]?.replayLink ?: "")
      }
    }
    existingList.forEach { logger.info(it.toString())}
    val fileWriter = FileWriter("data/time-slot-data.csv")
    val sbc = StatefulBeanToCsvBuilder<TimeSlot>(fileWriter)
      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
      .build()
    sbc.write(existingList)
    fileWriter.close()

  }

  fun fullReplayLink(id: String) : String{
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
