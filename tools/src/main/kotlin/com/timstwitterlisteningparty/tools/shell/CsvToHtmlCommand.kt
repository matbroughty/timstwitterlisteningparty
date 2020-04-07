package com.timstwitterlisteningparty.tools.shell

import com.opencsv.bean.CsvToBeanBuilder
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import org.slf4j.LoggerFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.io.File
import java.io.FileReader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap
import kotlin.collections.set


@ShellComponent
class CsvToHtmlCommand {
  private val logger = LoggerFactory.getLogger(javaClass)


  @ShellMethod("Produces the completed-time-slots.html, date-tbd-time-slots.html and the upcoming-time-slots.html files from a csv file - defaults to using time-slots-data.csv")
  fun html(@ShellOption("-f", "--file", defaultValue = "time-slot-data.csv") file: String,
           @ShellOption("-l", "--log", defaultValue = "false") log: Boolean): String {
    return "The html file was created ${createFiles(file, log)}"
  }

  fun createFiles(file: String, log: Boolean): String {
    logger.debug("File is {} and logging is {}", file, log)
    // default file to read
    var fileName = "time-slot-data.csv"
    if (file.isEmpty()) {
      logger.warn("No arguments passed defaulting to {}", fileName)
    } else {
      fileName = file
    }
    val beans: List<TimeSlot> = CsvToBeanBuilder<TimeSlot>(FileReader(fileName))
      .withType(TimeSlot::class.java).withIgnoreEmptyLine(true).build().parse()
    if(log) {
      beans.forEach { logger.debug("Read in Bean {}", it) }
    }
    val tbd = beans.stream()
      .filter { it.date.year == 1970 }.collect(Collectors.toList())
    val completed = beans.stream()
      .filter { it.date.year != 1970 && it.date.toLocalDate().isBefore(LocalDate.now()) }
      .collect(Collectors.toList())
    val upcoming = beans.stream()
      .filter { it.date.year != 1970 && it.date.toLocalDate().isBefore(LocalDate.now()).not() }
      .collect(Collectors.toList())
    if(log) {
      tbd.forEach { logger.info("Dates to be confirmed {}", it) }
      completed.forEach { logger.info("Completed listening {}", it) }
      upcoming.forEach { logger.info("Upcoming listening {}", it) }
    }
    val upcomingHtml = buildTable(upcoming, false, tbd = false)
    File("upcoming-time-slots.html").writeText(upcomingHtml)
    val  dateTbdHtml = buildTable(tbd, false, tbd = true)
    File("date-tbd-time-slots.html").writeText(dateTbdHtml)
    val completedHtml = buildTable(completed, true, tbd = false)
    File("completed-time-slots.html").writeText(completedHtml)
    if(log){
      logger.info("Upcoming\n {} \nDateTbd \n{} \ncompleted\n {}", upcomingHtml, dateTbdHtml, completedHtml)
    }
    return upcomingHtml.plus(dateTbdHtml).plus(completed)

  }

  private fun buildTable(slots: List<TimeSlot>, completed: Boolean, tbd: Boolean): String {
    var section = "<section class=\"post\">\n"
    val sortedSlots = if (tbd) slots.sortedBy { it.band } else slots.sortedBy { it.date }
    logger.debug("Sorted slots for completed {} and tbd {}", completed, tbd)
    sortedSlots.forEach { logger.debug("Time listening {}", it) }
    if (!tbd) {
      val first = sortedSlots.first()
      val startingMonday = first.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
      val last = sortedSlots.last()
      logger.info("first date {} startingMonday {} last date {}", first.date, startingMonday, last.date)
      val map: HashMap<LocalDate, ArrayList<TimeSlot>> = HashMap()
      var start = startingMonday
      while (start.isAfter(last.date).not()) {
        map[start.toLocalDate()] = ArrayList()
        start = start.plusWeeks(1)
      }
      map.keys.sortedBy { it }.forEach { logger.debug("key is {}", it) }
      sortedSlots.forEach { map[it.date.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))]?.add(it) }

      map.keys.stream().sorted().map { processTable(it, map[it], completed) }.collect(Collectors.toList()).forEach { section = section.plus(it) }
    } else {
      section = section.plus(processTable(null, sortedSlots, completed))
    }
    return section.plus("\n</section>")
  }

  private fun processTable(monday: LocalDate?, rows: List<TimeSlot>?, completed: Boolean): String {

    var h2Value = "Upcoming Events - Dates to be confirmed"

    if (monday != null) {
      val df = DateTimeFormatter.ofPattern("EEEE, MMMM d")
      h2Value = if (completed || previousWeek(monday)) "Week that commenced ${monday.format(df)}" else "Week commencing ${monday.format(df)}"
    }
    var htmlTable = "\n          <header class=\"post-header\">\n" +
      "            <h2 class=\"post-title\">$h2Value</h2>\n" +
      "\n" +
      "            <div class=\"scroll-table\">\n" +
      "              <table width=\"100%\" class=\"pure-table\">\n" +
      "                <thead>\n" +
      "                <tr>\n" +
      "                  <th width=\"20%\">Day</th>\n" +
      "                  <th width=\"10%\">Time</th>\n" +
      "                  <th width=\"30%\">Band</th>\n" +
      "                  <th width=\"25%\">Album</th>\n" +
      "                  <th width=\"15%\">Twitter Link</th>\n" +
      "                </tr>\n" +
      "                </thead>\n" +
      "                <tbody>"


    rows?.forEach { htmlTable = htmlTable.plus(it.buildHtmlRow()) }


    return htmlTable.plus("\n                </tbody>\n" +
      "              </table>\n" +
      "            </div>\n" +
      "            \n" +
      "          </header>")

  }


  /**
   * If this week or a week in the future return true else false
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
