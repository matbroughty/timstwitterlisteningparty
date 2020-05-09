package com.timstwitterlisteningparty.tools.parser

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream
import java.io.StringWriter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

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
      .collect(Collectors.toList())
    val upcoming = beans.stream()
      .filter { it.isoDate.year != 1970 && it.isoDate.toLocalDate().isBefore(LocalDate.now()).not() }
      .collect(Collectors.toList())
    tbd.forEach { logger.debug("Dates to be confirmed {}", it) }
    completed.forEach { logger.debug("Completed listening {}", it) }
    upcoming.forEach { logger.debug("Upcoming listening {}", it) }
    val upcomingHtml = buildTable(upcoming, false, tbd = false)
    val upcomingFile = File("snippets/upcoming-time-slots.html")
    val upcomingHtmlCard = buildTableCard(upcoming)
    val upcomingFileCard = File("snippets/upcoming-time-slots-card.html")
    val dateTbdHtml = buildTbcCards(tbd)
    val dateTbdFile = File("snippets/date-tbd-time-slots.html")
    val completedHtml = buildTable(completed, true, tbd = false)
    val completedFile = File("snippets/completed-time-slots.html")
    val allOneTableHtml = buildTable(beans, completed = true, tbd = true, all = true)
    //val allOneTableHtml = buildAllTable(beans)
    val allOneTableFile = File("snippets/all-time-slots.html")
    // if called from Lambda we can't write to the file
    if (writeToFile) {
      completedFile.writeText(completedHtml)
      upcomingFile.writeText(upcomingHtml)
      dateTbdFile.writeText(dateTbdHtml)
      allOneTableFile.writeText(allOneTableHtml)
      upcomingFileCard.writeText(upcomingHtmlCard)
    }
    logger.debug("Upcoming\n {} \nDateTbd \n{} \ncompleted\n {} \nAll \n{}", upcomingHtml, dateTbdHtml, completedHtml, allOneTableHtml)

    return mapOf(
      Pair("snippets/${upcomingFile.name}", upcomingHtml),
      Pair("snippets/${dateTbdFile.name}", dateTbdHtml),
      Pair("snippets/${completedFile.name}", completedHtml),
      Pair("snippets/${allOneTableFile.name}", allOneTableHtml),
      Pair("snippets/${upcomingFileCard.name}", upcomingHtmlCard))
  }

  private fun buildAllTable(beans: List<TimeSlot>): String {
    val template = FreeMarkerUtils().getFreeMarker(ALL_FTL)
    val input: Map<String, List<TimeSlot>> = mapOf(Pair("all_list", beans.sortedBy { it.band }))
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


  private fun buildTable(slots: List<TimeSlot>, completed: Boolean, tbd: Boolean, all: Boolean = false): String {
    var section = "<section class=\"post\">\n"
    val sortedSlots = if (tbd) slots.sortedBy { it.band } else slots.sortedBy { it.isoDate }
    logger.debug("Sorted slots for completed {} and tbd {}", completed, tbd)
    sortedSlots.forEach { logger.debug("Time listening {}", it) }
    if (!all && !tbd) {
      val first = sortedSlots.first()
      val startingMonday = first.isoDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
      val last = sortedSlots.last()
      logger.debug("first date {} startingMonday {} last date {}", first.isoDate, startingMonday, last.isoDate)
      val map: HashMap<LocalDate, ArrayList<TimeSlot>> = HashMap()
      var start = startingMonday
      while (start.isAfter(last.isoDate).not()) {
        map[start.toLocalDate()] = ArrayList()
        start = start.plusWeeks(1)
      }
      map.keys.sortedBy { it }.forEach { logger.debug("key is {}", it) }
      sortedSlots.forEach { map[it.isoDate.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))]?.add(it) }
      map.keys.stream().sorted(if (completed) reverseOrder() else naturalOrder())
        .map { pureTable(it, map[it], completed) }
        .collect(Collectors.toList())
        .forEach { section = section.plus(it) }
    } else {
      section = section.plus(pureTable(null, sortedSlots, completed, all))
    }
    return section.plus("\n</section>")
  }


  private fun pureTable(monday: LocalDate?, rows: List<TimeSlot>?, completed: Boolean, all: Boolean = false): String {

    var h2Value = if (all) "All Events - Searchable & Sortable" else " Upcoming Events - Dates to be confirmed"
    if (monday != null) {
      val df = DateTimeFormatter.ofPattern("EEEE, MMMM d")
      h2Value = if (completed || previousWeek(monday))
        "Week that commenced ${monday.format(df)}"
      else "Week commencing ${monday.format(df)}"
    }

    var icon = "<i class=\"fas fa-calendar-day\"></i>"
    if (completed) {
      icon = "<i class=\"fas fa-calendar-check\"></i>"
    }
    val tableId = if (all) "id=\"time-slots\"" else ""
    var htmlTable =
      "  <div class=\"card bg-light mb-2 border-dark \" style=\"max-width\">\n" +
        "    <div class=\"card-header\">$icon $h2Value</div>\n" +
        "    <div class=\"card-body p-0\">" +
        "            <div class=\"scroll-table\">\n" +
        "              <table $tableId width=\"100%\" class=\"pure-table\">\n" +
        "                <thead>\n" +
        "                <tr>\n" +
        "                  <th width=\"15%\">Day</th>\n" +
        "                  <th width=\"5%\">Time</th>\n" +
        "                  <th width=\"35%\">Band</th>\n" +
        "                  <th width=\"35%\">Album</th>\n" +
        "                  <th width=\"10%\">Link</th>\n" +
        "                </tr>\n" +
        "                </thead>\n" +
        "                <tbody>"

    // add each row
    rows?.forEach { htmlTable = htmlTable.plus(it.buildHtmlRow()) }
    // the script for the search needs to be in the snippet
    if (all) {
      htmlTable = htmlTable.plus("<script>\n" +
        "    \$(document).ready(function() {\n" +
        "      \$('#time-slots').DataTable({\n" +
        "        \"paging\": false\n" +
        "      });\n" +
        "    });\n" +
        "\n" +
        "</script>")
    }
    // close table and divs
    return htmlTable.plus("\n                </tbody>\n" +
      "              </table>\n   </div></div></div>\n")
  }


  /**
   * If this week or a week in the future return false else true
   */
  private fun previousWeek(monday: LocalDate): Boolean {
    if (monday.isEqual(LocalDate.now())) {
      return false
    }
    val woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()
    val weekNumber: Int = LocalDate.now().get(woy)
    val mondayWeekNum: Int = monday.get(woy)
    // not this week
    if (monday.isBefore(LocalDate.now()) && weekNumber != mondayWeekNum) {
      return true
    }
    // else must be the weeks before
    return false
  }

  private fun buildTableCard(slots: List<TimeSlot>): String {
    val sortedSlots = slots.sortedBy { it.isoDate }
    var section = "<section class=\"post\">\n<div class=\"container-fluid\">"

    var hr = ""
    var date = sortedSlots.first().isoDate
    section = section.plus("      <div class=\"card d mb-3 border-dark\" style=\"width: 100%;\">\n" +
      "        <div class=\"card-header font-weight-bold\">\n" +
      "          <i class=\"fas fa-calendar-day\"></i> ${date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))} \n" +
      "        </div>")


    sortedSlots.forEach {

      // new card header required if we have moved on
      if (it.isoDate.toLocalDate().isAfter(date.toLocalDate())) {
        section = section.plus("      </div><div class=\"card d mb-3\" style=\"width: 100%;\">\n" +
          "        <div class=\"card-header font-weight-bold\">\n" +
          "          <i class=\"fas fa-calendar-day\"></i> ${it.isoDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))} \n" +
          "        </div>")
      } else {
        section = section.plus(hr)
        hr = "<hr/>"
      }
      // build the card body
      section = section.plus(it.buildHtmlCardBody())
      date = it.isoDate
    }

    return section.plus("\n</div></div>\n</section>")
  }


}
