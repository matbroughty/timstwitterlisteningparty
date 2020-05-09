package com.timstwitterlisteningparty.tools.parser

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.*
import java.util.*


/**
 * Reads the data/time-slot-data.csv and with the resultant list of [TimeSlot]
 * writes the pages out - one for each row with a twitter list
 * Returns a List of Pairs one for each page generated - name of file (key) and content of file (html) - this
 * can then be used in S3
 */
@Component
class CollectionsHtmlPagesCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun createTwitterListPages(fileName: String = "data/time-slot-data.csv", inputStream: InputStream? = null,
                             writeToFile: Boolean = false, newFileName: String = fileName): List<Pair<String, String>> {


    val existingList = TimeSlotReader(fileName, inputStream).timeSlots

    val template = FreeMarkerUtils().getFreeMarker(COLLECTIONS_FTL)
    val pageMap = mutableListOf<Pair<String, String>>()
    // create a new page for each available replay
    existingList.filter { it.replayLink.isNotEmpty() }.forEach { it ->
      // the data
      val input: Map<String, TimeSlot> = mapOf(Pair("slot", it))
      try {
        if (writeToFile) {
          val pageFileName = "pages/list/collection_${it.replayId()}.html"
          val fileWriter: Writer = FileWriter(File(pageFileName))
          template.process(input, fileWriter)
          val htmlStr = StringWriter()
          template.process(input, htmlStr)
          pageMap.add(Pair(pageFileName, htmlStr.toString()))
        }
      } catch (e: Exception) {
        logger.warn("issue writing collection file for TimeSlot $it")
      }
    }

    return pageMap

  }
}
