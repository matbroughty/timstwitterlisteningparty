package com.timstwitterlisteningparty.tools.parser

import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileWriter
import java.util.stream.Collectors


/**
 * Takes the existing HTML file(s) (just index.html really) and creates
 * a csv file from it with the date strings converted to Iso date/time
 */
@Order(1)
@Component
class HtmlToCsvRunner() : CommandLineRunner {
  private val logger = LoggerFactory.getLogger(javaClass)
  override fun run(vararg args: String?) {
    logger.info("args passed in {} ", args)

    var fileName = "index.html"
    if (args.isEmpty()) {
      logger.warn("No arguments passed defaulting to {}", fileName)
    } else {
      fileName = args[0] ?: ""
    }

    logger.info("Parsing File from '{}'", fileName)
    val input = File(fileName)
    val doc: Document = Jsoup.parse(input, "UTF-8")

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
