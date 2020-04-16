package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader

@Component
class BookReviewFileCreator : HtmlFileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun createFiles(fileName: String, inputStream: InputStream?, writeToFile: Boolean): String {

    val csvToBeanBuilder: CsvToBeanBuilder<BookReview> =
      if (inputStream != null) CsvToBeanBuilder<BookReview>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<BookReview>(FileReader(fileName))
      }

    val beans: List<BookReview> = csvToBeanBuilder.withType(BookReview::class.java).withSkipLines(1).withIgnoreEmptyLine(true).build().parse()
    beans.forEach { logger.info("Read in Bean {}", it) }
    val storeHtml = buildTable(beans)
    logger.info("Book Reviews html {}", storeHtml)
    val file = File("snippets/book-reviews-shops.html")
    if (writeToFile) {
      file.writeText(storeHtml)
    }
    return storeHtml
  }

  private fun buildTable(slots: List<BookReview>): String {
    var section = "<section class=\"post\">\n"
    //val sortedStores = slots.sortedBy { it.title }
    logger.debug("Stores for {}", slots)
    section = section.plus(pureTable(slots))
    return section.plus("\n</section>")
  }


  private fun pureTable(rows: List<BookReview>?): String {

    var h2Value = "Book Reviews & Shops"

    var icon = "<i class=\"fas fa-book-open\"></i>"

    val tableId = "id=\"book-reviews\""
    var htmlTable =
      "  <div class=\"card bg-light mb-2 border-dark \" style=\"max-width\">\n" +
        "    <div class=\"card-header\">$icon $h2Value</div>\n" +
        "    <div class=\"card-body p-0\">" +
        "            <div class=\"scroll-table\">\n" +
        "              <table $tableId width=\"100%\" class=\"pure-table\">\n" +
        "                <thead>\n" +
        "                <tr>\n" +
        "                  <th width=\"15%\">Author</th>\n" +
        "                  <th width=\"15%\">Title</th>\n" +
        "                  <th width=\"60%\">Description</th>\n" +
        "                  <th width=\"5%\">Review</th>\n" +
        "                  <th width=\"5%\">Buy Here</th>\n" +
        "                </tr>\n" +
        "                </thead>\n" +
        "                <tbody>"

    // add each row
    rows?.forEach { htmlTable = htmlTable.plus(it.buildHtmlRow()) }
//    htmlTable = htmlTable.plus("<script>\n" +
//      "    \$(document).ready(function() {\n" +
//      "      \$('#book-reviews').DataTable({\n" +
//      "        \"paging\": false\n" +
//      "      });\n" +
//      "    });\n" +
//      "\n" +
//      "</script>")
    // close table and divs
    return htmlTable.plus("\n                </tbody>\n" +
      "              </table>\n   </div></div></div>\n")
  }

}
