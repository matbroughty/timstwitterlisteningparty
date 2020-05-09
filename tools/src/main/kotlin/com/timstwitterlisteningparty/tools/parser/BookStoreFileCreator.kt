package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.*

@Component
class BookStoreFileCreator : HtmlFileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun createFiles(fileName: String, inputStream: InputStream?, writeToFile: Boolean): String {
    val csvToBeanBuilder: CsvToBeanBuilder<BookStore> =
      if (inputStream != null) CsvToBeanBuilder<BookStore>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<BookStore>(FileReader(fileName))
      }
    val shops: List<BookStore> = csvToBeanBuilder.withType(BookStore::class.java)
      .withSkipLines(1).withIgnoreEmptyLine(true).build().parse()
    shops.forEach { logger.debug("Read in Book Shop {}", it) }
    val template = FreeMarkerUtils().getFreeMarker(BOOK_SHOPS_FTL)
    val input: Map<String, List<BookStore>> = mapOf(Pair("shops", shops.sortedBy { it.name }))
    val htmlStr = StringWriter()
    template.process(input, htmlStr)
    logger.debug("Store html {}", htmlStr)
    val file = File("snippets/book-stores.html")
    if (writeToFile) {
      file.writeText(htmlStr.toString())
    }
    return htmlStr.toString()
  }

}
