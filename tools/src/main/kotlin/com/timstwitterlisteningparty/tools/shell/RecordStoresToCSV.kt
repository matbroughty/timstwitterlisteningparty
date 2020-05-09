package com.timstwitterlisteningparty.tools.shell

import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.timstwitterlisteningparty.tools.parser.RecordStore
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import java.io.FileWriter
import kotlin.streams.toList


/**
 * Pulls record store info from "https://indieretail.beggars.com/uk/" and puts it into data/generated-record-store-data.csv
 * as a starting point for the data/record-store-data.csv which is then manually managed
 */
@ShellComponent
class RecordStoresToCSV {
  private val logger = LoggerFactory.getLogger(javaClass)

  @ShellMethod("Builds up a base record store file generated-record-store-data.csv from the https://indieretail.beggars.com/uk/")
  fun storescsv(): String {
    return "The generated-record-store-data.csv file was created:${createFile()} "
  }

  fun createFile(): Boolean {
    val doc: Document = Jsoup.connect("https://indieretail.beggars.com/uk/").get()
    val stores = doc.select("li")
      .stream()
      .filter { it.text() != "Mailorder Available" }
      .filter { it.text() != "Pick Up Available" }
      .filter { it.text() != "Local Delivery Available" }
      .filter { it.children().`is`("a").not() }
      .map { it.buildCsvRow() }
      .filter { it != null }
      .toList()

    stores.forEach { logger.info("retrieved store {}", it) }

    val fileWriter = FileWriter("data/generated-record-store-data.csv")
    val sbc = StatefulBeanToCsvBuilder<RecordStore>(fileWriter)
      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
      .build()

    sbc.write(stores)

    fileWriter.close()


    return stores.isNotEmpty()

  }

  private fun Element?.buildCsvRow(): RecordStore? {
    if (this != null) {
      logger.info("row {}", html())

      return RecordStore(
        name = StringUtils.substringBefore(select("p.store-title").first().text(), " - Visit website"),
        address = select("p.store-address").first().text(),
        webSite = select("p")[0].select("a").attr("href")
      )
    }
    return null
  }

}
