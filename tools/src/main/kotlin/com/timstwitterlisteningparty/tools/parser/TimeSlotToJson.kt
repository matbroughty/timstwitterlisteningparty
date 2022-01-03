package com.timstwitterlisteningparty.tools.parser

import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicInteger

/**
 * Turns the TimeSlot file into a json file.
 * The json format is that of a dynamodb table @see TIMESLOT_JSON_FTL
 */
class TimeSlotToJson : HtmlFileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun createFiles(fileName: String, inputStream: InputStream?, writeToFile: Boolean): Int {
    val timeSlots = TimeSlotReader(fileName, inputStream).timeSlots
    val template = FreeMarkerUtils().getFreeMarker(TIMESLOT_JSON_FTL)

    // aws will only accept 25 puts - i.e. 25 items in one go so split.  Only parties with a ttlp num
    val chunks = timeSlots.filter { it.hasNumber() }.sortedBy { it.isoDate }.chunked(25)

    val counter = AtomicInteger()
    chunks.forEach {
      val jsonStr = StringWriter()
      val input: Map<String, List<TimeSlot>> = mapOf(Pair("slots", it))
      template.process(input, jsonStr)
      logger.debug("Time Slot json {}", jsonStr.toString())
      val file = File("data/dynamodb/time-slot${counter.incrementAndGet()}.json")
      if (writeToFile) {
        file.writeText(jsonStr.toString())
      }
    }

    // how many pages of 25 puts to dynamo db?
    return counter.get()
  }
}
