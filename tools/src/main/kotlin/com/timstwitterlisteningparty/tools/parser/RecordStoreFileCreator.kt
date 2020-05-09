package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.*

@Component
class RecordStoreFileCreator : HtmlFileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun createFiles(fileName: String, inputStream: InputStream?, writeToFile: Boolean): String {

    val csvToBeanBuilder: CsvToBeanBuilder<RecordStore> =
      if (inputStream != null) CsvToBeanBuilder<RecordStore>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<RecordStore>(FileReader(fileName))
      }

    val shops: List<RecordStore> = csvToBeanBuilder.withType(RecordStore::class.java).withSkipLines(1).withIgnoreEmptyLine(true).build().parse()
    shops.forEach { logger.debug("Read in Bean {}", it) }

    val template = FreeMarkerUtils().getFreeMarker(RECORD_SHOPS_FTL)
    val input: Map<String, List<RecordStore>> = mapOf(Pair("shops", shops.sortedBy { it.name }))
    val htmlStr = StringWriter()
    template.process(input, htmlStr)

    //val storeHtml = buildTable(beans)
    logger.debug("Store html {}", htmlStr.toString())
    val file = File("snippets/record-stores.html")
    if (writeToFile) {
      file.writeText(htmlStr.toString())
    }
    return htmlStr.toString()
  }

}
