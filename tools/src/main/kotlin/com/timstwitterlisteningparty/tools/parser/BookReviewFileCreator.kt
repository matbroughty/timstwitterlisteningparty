package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.*

@Component
class BookReviewFileCreator : HtmlFileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun createFiles(fileName: String, inputStream: InputStream?, writeToFile: Boolean): String {

    val csvToBeanBuilder: CsvToBeanBuilder<BookReview> =
      if (inputStream != null) CsvToBeanBuilder<BookReview>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<BookReview>(FileReader(fileName))
      }

    val reviews: List<BookReview> = csvToBeanBuilder.withType(BookReview::class.java)
      .withSkipLines(1).withIgnoreEmptyLine(true).build().parse()
    reviews.forEach { logger.debug("Read in Bean {}", it) }

    val template = FreeMarkerUtils().getFreeMarker(BOOK_REVIEWS_FTL)
    val input: Map<String, List<BookReview>> = mapOf(Pair("reviews",reviews))
    val htmlStr = StringWriter()
    template.process(input, htmlStr)
    logger.debug("Reviews html {}", htmlStr)
    val file = File("snippets/book-reviews-shops.html")
    if (writeToFile) {
      file.writeText(htmlStr.toString())
    }
    return htmlStr.toString()
  }

}
