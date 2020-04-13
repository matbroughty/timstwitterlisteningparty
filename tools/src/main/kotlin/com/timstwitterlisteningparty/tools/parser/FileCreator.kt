package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

@Component
class FileCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun createFiles(fileName: String = "time-slot-data.csv", inputStream: InputStream? = null, writeToFile : Boolean = true): Map<String, String> {
    var csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> =
      if (inputStream != null) CsvToBeanBuilder<TimeSlot>(InputStreamReader(inputStream)) else {
          CsvToBeanBuilder<TimeSlot>(FileReader(fileName))
      }
    val beans: List<TimeSlot> = csvToBeanBuilder.withType(TimeSlot::class.java).withIgnoreEmptyLine(true).build().parse()
    beans.forEach { logger.info("Read in Bean {}", it) }
    val tbd = beans.stream()
      .filter { it.isoDate.year == 1970 }.collect(Collectors.toList())
    val completed = beans.stream()
      .filter { it.isoDate.year != 1970 && it.isoDate.toLocalDate().isBefore(LocalDate.now()) }
      .collect(Collectors.toList())
    val upcoming = beans.stream()
      .filter { it.isoDate.year != 1970 && it.isoDate.toLocalDate().isBefore(LocalDate.now()).not() }
      .collect(Collectors.toList())
    tbd.forEach { logger.info("Dates to be confirmed {}", it) }
    completed.forEach { logger.info("Completed listening {}", it) }
    upcoming.forEach { logger.info("Upcoming listening {}", it) }
    val upcomingHtml = buildTable(upcoming, false, tbd = false)
    val upcomingFile = File("upcoming-time-slots.html")
    val dateTbdHtml = buildTable(tbd, false, tbd = true)
    val dateTbdFile = File("date-tbd-time-slots.html")
    val completedHtml = buildTable(completed, true, tbd = false)
    val completedFile = File("completed-time-slots.html")
    val allOneTableHtml = buildTable(beans, completed = true, tbd= true, all = true)
    val allOneTableFile = File("all-time-slots.html")
    // if called from Lambda we can't write to the file
    if(writeToFile) {
      completedFile.writeText(completedHtml)
      upcomingFile.writeText(upcomingHtml)
      dateTbdFile.writeText(dateTbdHtml)
      allOneTableFile.writeText(allOneTableHtml)
    }
    logger.info("Upcoming\n {} \nDateTbd \n{} \ncompleted\n {} \nAll \n{}", upcomingHtml, dateTbdHtml, completedHtml, allOneTableHtml)

    return mapOf(
      Pair(upcomingFile.name,upcomingHtml),
      Pair(dateTbdFile.name,dateTbdHtml),
      Pair(completedFile.name,completedHtml),
      Pair(allOneTableFile.name,allOneTableHtml))
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
      logger.info("first date {} startingMonday {} last date {}", first.isoDate, startingMonday, last.isoDate)
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

    var h2Value = if(all) "All Events - Searchable & Sortable" else " Upcoming Events - Dates to be confirmed"
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
    val tableId = if(all) "id=\"time-slots\"" else ""
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
    if(all){
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


}
