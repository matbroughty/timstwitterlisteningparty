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
import org.springframework.shell.standard.ShellOption
import java.io.FileWriter
import java.util.stream.Collectors


@ShellComponent
class HtmlToCsvCommand {
  private val logger = LoggerFactory.getLogger(javaClass)

  @ShellMethod("Produces the generated-time-slot.date.csv file from the url https://timstwitterlisteningparty.com/time-slots.html by default")
  fun csv(@ShellOption("-u", "--url", defaultValue = "https://timstwitterlisteningparty.com/time-slots.html") url: String): String {
    return "The generated-time-slot.date.csv file was created: ${createFile(url)}"
  }


  fun createFile(url: String): Boolean {
    logger.info("args passed in {} ", url)
    if (url.isEmpty()) {
      logger.warn("No arguments passed defaulting to {}", url)
    }
    logger.info("Parsing URL from '{}'", url)
    val doc: Document = Jsoup.connect(url).get()

    val csvRows = doc.select("tr")
      .stream()
      .filter { it.children().`is`("th").not() }
      .map { it.buildCsvRow() }
      .filter { it != null }
      .collect(Collectors.toList())

    csvRows.forEach { logger.info("TimeSlot is: {}", it) }
    val fileWriter = FileWriter("generated-time-slot-data.csv")
    val sbc = StatefulBeanToCsvBuilder<TimeSlot>(fileWriter)
      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
      .build()
    sbc.write(csvRows)
    fileWriter.close()

    return true
  }

  private fun Element?.buildCsvRow(): TimeSlot? {
    if (this != null) {
      logger.debug("row {}", html())
      return TimeSlot(
        select("td")[0].text(),
        select("td")[1].text(),
        select("td")[2].text(),
        select("td")[3].text(),
        select("td")[4].select("a").attr("href")
      )
    }
    return null
  }


}
