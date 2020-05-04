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
    beans.forEach { logger.debug("Read in Bean {}", it) }
    val storeHtml = buildTable(beans)
    logger.debug("Book Reviews html {}", storeHtml)
    val file = File("snippets/book-reviews-shops.html")
    if (writeToFile) {
      file.writeText(storeHtml)
    }
    return storeHtml
  }

  private fun buildTable(slots: List<BookReview>): String {
    var section = "<div class=\"container-fluid\">\n"
    logger.debug("Stores for {}", slots)
    section = section.plus(pureTable(slots))
    return section.plus("\n</div>")
  }

  private fun pureTable(rows: List<BookReview>): String {
    return rows.joinToString(separator = "") { it.buildHtmlRow() }

  }

}
