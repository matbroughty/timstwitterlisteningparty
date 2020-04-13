package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

@Component
class RecordStoreFileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun createFiles(fileName: String = "record-store-data.csv"): String {
    val csvToBeanBuilder: CsvToBeanBuilder<RecordStore> = CsvToBeanBuilder<RecordStore>(FileReader(fileName))
    val beans: List<RecordStore> = csvToBeanBuilder.withType(RecordStore::class.java).withSkipLines(1).withIgnoreEmptyLine(true).build().parse()
    beans.forEach { logger.info("Read in Bean {}", it) }
    val storeHtml = buildTable(beans)
    logger.info("Store html {}",storeHtml)
    File("snippets/record-stores.html").writeText(storeHtml)
    return storeHtml
  }

  private fun buildTable(slots: List<RecordStore>): String {
    var section = "<section class=\"post\">\n"
    val sortedStores = slots.sortedBy { it.name}
    logger.debug("Sorted stores for {}", sortedStores)
    section = section.plus(pureTable(sortedStores))
    return section.plus("\n</section>")
  }


  private fun pureTable(rows: List<RecordStore>?): String {

    var h2Value =  "Record Stores - Mail Order.  See Map below"

    var icon = "<i class=\"fas fa-record-vinyl\"></i>"

    val tableId = "id=\"record-stores\""
    var htmlTable =
      "  <div class=\"card bg-light mb-2 border-dark \" style=\"max-width\">\n" +
        "    <div class=\"card-header\">$icon $h2Value</div>\n" +
        "    <div class=\"card-body p-0\">" +
        "            <div class=\"scroll-table\">\n" +
        "              <table $tableId width=\"100%\" class=\"pure-table\">\n" +
        "                <thead>\n" +
        "                <tr>\n" +
        "                  <th width=\"30%\">Shop</th>\n" +
        "                  <th width=\"50%\">Address</th>\n" +
        "                  <th width=\"10%\">Website</th>\n" +
        "                  <th width=\"10%\">Twitter</th>\n" +
        "                </tr>\n" +
        "                </thead>\n" +
        "                <tbody>"

    // add each row
    rows?.forEach { htmlTable = htmlTable.plus(it.buildHtmlRow()) }
      htmlTable = htmlTable.plus("<script>\n" +
        "    \$(document).ready(function() {\n" +
        "      \$('#record-stores').DataTable({\n" +
        "        \"paging\": false\n" +
        "      });\n" +
        "    });\n" +
        "\n" +
        "</script>")
    // close table and divs
    return htmlTable.plus("\n                </tbody>\n" +
      "              </table>\n   </div></div></div>\n")
  }

}
