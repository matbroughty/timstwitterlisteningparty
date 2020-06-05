package com.timstwitterlisteningparty.tools.parser

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream
import java.io.StringWriter
import java.time.LocalDate
import java.util.stream.Collectors

/**
 * Builds the following files based on the data/time-slot-data.csv [TimeSlotReader]:
 *
 *   * snippets/upcoming-time-slots-card.html used on index.html from the [UPCOMING_FTL]
 *   * snippets/date-tbd-time-slots.html used on the tbc.html from the [TBC_FTL]
 *   * snippets/completed-time-slots.html used on the archive.html from the [ARCHIVE_FTL]
 *   * snippets/all-time-slots.html used on the all.html from the [ALL_FTL]
 *   * snippets/wall.html  from the [WALL_FTL]
 */
@Component
class TimeSlotFileCreator : HtmlFileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  override fun createFiles(fileName: String, inputStream: InputStream?, writeToFile: Boolean): Map<String, String> {

    val beans = TimeSlotReader(fileName, inputStream).timeSlots
    beans.forEach { logger.debug("Read in Bean {}", it) }
    val tbd = beans.stream()
      .filter { it.isoDate.year == 1970 }.collect(Collectors.toList())
    val completed = beans.stream()
      .filter { it.isoDate.year != 1970 && it.isoDate.toLocalDate().isBefore(LocalDate.now()) }
      .filter{it.replayLink.isNotBlank()}
      .collect(Collectors.toList())
    val upcoming = beans.stream()
      .filter { it.isoDate.year != 1970 && it.isoDate.toLocalDate().isBefore(LocalDate.now()).not() }
      .collect(Collectors.toList())
    tbd.forEach { logger.debug("Dates to be confirmed {}", it) }
    completed.forEach { logger.debug("Completed listening {}", it) }
    upcoming.forEach { logger.debug("Upcoming listening {}", it) }
    // the new card based table
    val upcomingHtmlCard = buildUpcomingCards(upcoming)
    val upcomingFileCard = File("snippets/upcoming-time-slots-card.html")
    val dateTbdHtml = buildTbcCards(tbd)
    val dateTbdFile = File("snippets/date-tbd-time-slots.html")
    val completedHtml = buildCompletedTableCards(completed)
    val completedFile = File("snippets/completed-time-slots.html")
    val allOneTableHtml = buildAllTable(beans)
    val allOneTableFile = File("snippets/all-time-slots.html")
    val wallHtml = buildWallHtml(completed, upcoming)
    val wallFile = File("snippets/wall.html")

    // if called from Lambda we can't write to the file
    if (writeToFile) {
      completedFile.writeText(completedHtml)
      dateTbdFile.writeText(dateTbdHtml)
      allOneTableFile.writeText(allOneTableHtml)
      upcomingFileCard.writeText(upcomingHtmlCard)
      wallFile.writeText(wallHtml)
    }
    logger.debug("Upcoming\n {} \nDateTbd \n{} \ncompleted\n {} \nAll \n{}", upcomingHtmlCard, dateTbdHtml, completedHtml, allOneTableHtml)
    return mapOf(
      Pair("snippets/${dateTbdFile.name}", dateTbdHtml),
      Pair("snippets/${completedFile.name}", completedHtml),
      Pair("snippets/${allOneTableFile.name}", allOneTableHtml),
      Pair("snippets/${upcomingFileCard.name}", upcomingHtmlCard),
      Pair("pages/${wallFile.name}", wallHtml)
      )
  }

  private fun buildWallHtml(completed: List<TimeSlot>, upcoming: List<TimeSlot>): String {
    val template = FreeMarkerUtils().getFreeMarker(WALL_FTL)
    val completedList: List<List<TimeSlot>> = completed.filter { it.tweeterLinkList().isNotEmpty() && it.spotifyImgLinkSmall.isNotEmpty()}.sortedBy { it.isoDate }.chunked(12).toList()
    val upcomingList: List<List<TimeSlot>> = upcoming.filter { it.spotifyImgLinkSmall.isNotEmpty()}.sortedBy { it.isoDate }.chunked(12).toList()

    val input: Map<String, List<List<TimeSlot>>> = mapOf(Pair("completed_list", completedList), Pair("upcoming_list", upcomingList))
    val htmlStr = StringWriter()
    template.process(input, htmlStr)
    return htmlStr.toString()
  }

  private fun buildUpcomingCards(upcoming: List<TimeSlot>): String {
    val template = FreeMarkerUtils().getFreeMarker(UPCOMING_FTL)
    val input: Map<String, Any> = mapOf(
      Pair("upcoming_list", upcoming.sortedBy { it.isoDate }),
      Pair("startDate", upcoming.first().isoDate),
      Pair("startDateFormatted", upcoming.first().dateDisplayString())
      )
    val htmlStr = StringWriter()
    template.process(input, htmlStr)
    return htmlStr.toString()
  }

  private fun buildAllTable(beans: List<TimeSlot>): String {
    val template = FreeMarkerUtils().getFreeMarker(ALL_FTL)
    val input: Map<String, List<TimeSlot>> = mapOf(Pair("all_list", beans.sortedBy { it.album }))
    val htmlStr = StringWriter()
    template.process(input, htmlStr)
    return htmlStr.toString()
  }

  /**
   * Uses template tbc.ftl to create the tbc card
   */
  private fun buildTbcCards(tbd: List<TimeSlot>): String {
    val template = FreeMarkerUtils().getFreeMarker(TBC_FTL)
    val input: Map<String, List<TimeSlot>> = mapOf(Pair("tbc_list", tbd.sortedBy { it.band }))
    val htmlStr = StringWriter()
    template.process(input, htmlStr)
    return htmlStr.toString()
  }

  private fun buildCompletedTableCards(completed: List<TimeSlot>): String {
    val template = FreeMarkerUtils().getFreeMarker(ARCHIVE_FTL)
    val input: Map<String, List<TimeSlot>> = mapOf(Pair("completed_list", completed.sortedByDescending{ it.isoDate }))
    val htmlStr = StringWriter()
    template.process(input, htmlStr)
    return htmlStr.toString()
  }

}
