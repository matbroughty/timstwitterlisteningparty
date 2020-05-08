package com.timstwitterlisteningparty.tools.shell

import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import java.io.FileWriter
import kotlin.streams.toList


@ShellComponent
class HtmlToCsvCommand {
  private val logger = LoggerFactory.getLogger(javaClass)

  @ShellMethod("Produces the generated-time-slot.date.csv file from the url https://timstwitterlisteningparty.com/time-slots.html, https://timstwitterlisteningparty.com/completed-time-slots.html and https://timstwitterlisteningparty.com/date-tbd-time-slots.html")
  fun csv(): String {
    return "The generated-time-slot.date.csv file was created: ${createFile()}"
  }

  fun createFile(): Boolean {

    val timeSlots = timeSlotList("https://timstwitterlisteningparty.com/snippets/time-slots.html")
    val completedTimeSlots = (timeSlotList("https://timstwitterlisteningparty.com/snippets/completed-time-slots.html"))
    val tbcTimeSlots = (timeSlotList("https://timstwitterlisteningparty.com/snippets/date-tbd-time-slots.html"))

    val fileWriter = FileWriter("data/generated-time-slot-data.csv")
    val sbc = StatefulBeanToCsvBuilder<TimeSlot>(fileWriter)
      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
      .build()
    sbc.write(timeSlots)
    sbc.write(completedTimeSlots)
    sbc.write(tbcTimeSlots)
    fileWriter.close()
    return true
  }

  private fun timeSlotList(url: String): List<TimeSlot?> {
    logger.info("Parsing URL from '{}'", url)
    val doc: Document = Jsoup.connect(url).get()
    return doc.select("tr")
      .stream()
      .filter { it.children().`is`("th").not() }
      .map { it.buildCsvRow() }
      .filter { it != null }
      .toList()
  }

  private fun Element?.buildCsvRow(): TimeSlot? {
    if (this != null) {
      logger.debug("row {}", html())
      return TimeSlot(
        dateStr = select("td")[0].text(),
        timeStr = select("td")[1].text(),
        band = select("td")[2].text(),
        album = select("td")[3].text(),
        link = select("td")[4].select("a").attr("href")
      )
    }
    return null
  }

}
