package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader

@Component
class BookStoreFileCreator : HtmlFileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun createFiles(fileName: String, inputStream: InputStream?, writeToFile: Boolean): String {

    val csvToBeanBuilder: CsvToBeanBuilder<BookStore> =
      if (inputStream != null) CsvToBeanBuilder<BookStore>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<BookStore>(FileReader(fileName))
      }

    val beans: List<BookStore> = csvToBeanBuilder.withType(BookStore::class.java).withSkipLines(1).withIgnoreEmptyLine(true).build().parse()
    beans.forEach { logger.debug("Read in Bean {}", it) }
    val storeHtml = buildTable(beans)
    logger.debug("Store html {}", storeHtml)
    val file = File("snippets/book-stores.html")
    if (writeToFile) {
      file.writeText(storeHtml)
    }
    return storeHtml
  }

  private fun buildTable(slots: List<BookStore>): String {
    var section = "<section class=\"post\">\n"
    val sortedStores = slots.sortedBy { it.name }
    logger.debug("Sorted stores for {}", sortedStores)
    section = section.plus(pureTable(sortedStores))
    return section.plus("\n</section>")
  }


  private fun pureTable(rows: List<BookStore>?): String {

    var h2Value = "Independent Book Shops - Mail Order.  See Map below"

    var icon = "<i class=\"fas fa-book-reader\"></i>"

    val tableId = "id=\"book-stores\""
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
        "                </tr>\n" +
        "                </thead>\n" +
        "                <tbody>"

    // add each row
    rows?.forEach { htmlTable = htmlTable.plus(it.buildHtmlRow()) }
    htmlTable = htmlTable.plus("<script>\n" +
      "    \$(document).ready(function() {\n" +
      "      \$('#book-stores').DataTable({\n" +
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
