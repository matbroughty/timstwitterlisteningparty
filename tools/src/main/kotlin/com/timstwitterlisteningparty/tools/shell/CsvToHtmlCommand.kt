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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.stream.Collectors

@ShellComponent
class CsvToHtmlCommand{
  private val logger = LoggerFactory.getLogger(javaClass)


  @ShellMethod("Produces the time-slots.html file from a csv file - defaults to using time-slots-data.csv")
  fun html(@ShellOption("-f", "--file", defaultValue = "time-slot-data.csv") file : String) : String{
    return "The html file was created ${createFile(file)}"
  }


  fun createFile(file: String)  : String{
    logger.info("args passed in {} ", file)
    // default file to read
    var fileName = "time-slot-data.csv"
    if (file.isEmpty()) {
      logger.warn("No arguments passed defaulting to {}", fileName)
    } else {
      fileName = file
    }
    val beans: List<TimeSlot> = CsvToBeanBuilder<TimeSlot>(FileReader(fileName))
      .withType(TimeSlot::class.java).build().parse()
    beans.forEach { logger.debug("Read in Bean {}", it) }
    val tbd = beans.stream().filter { it.date.year == 1970 }.collect(Collectors.toList())
    tbd.forEach { logger.debug("Dates to be confirmed {}", it) }
    val completed = beans.stream().filter { it.date.year != 1970 && it.date.isBefore(LocalDateTime.now()) }.collect(Collectors.toList())
    completed.forEach { logger.debug("Completed listening {}", it) }
    val upcoming = beans.stream().filter { it.date.year != 1970 && it.date.isAfter(LocalDateTime.now().minusHours(6)) }.collect(Collectors.toList())
    upcoming.forEach { logger.debug("Upcoming listening {}", it) }

    var htmlString = buildTable(upcoming, false, tbd = false)
    htmlString = htmlString.plus(buildTable(tbd, false, tbd = true))
    htmlString = htmlString.plus(buildTable(completed, true, tbd = false))
    logger.info("Html generated is {}", htmlString)
    File("time-slots.html").writeText(htmlString)
    return htmlString

  }

  private fun buildTable(slots: List<TimeSlot>, completed: Boolean, tbd: Boolean): String {

    var section = "<section class=\"post\">\n"
    val sortedSlots = if (tbd) slots.sortedBy { it.band } else slots.sortedBy { it.date }
    logger.info("Sorted slots for completed {} and tbd {}", completed, tbd)
    sortedSlots.forEach { logger.debug("Time listening {}", it) }
    if (!tbd) {
      val first = sortedSlots.first()
      val startingMonday = first.date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
      val last = sortedSlots.last()
      logger.info("first date {} startingMonday {} last date {}", first.date, startingMonday, last.date)
      val map: HashMap<LocalDate, ArrayList<TimeSlot>> = HashMap()
      var start = startingMonday
      while (start.isBefore(last.date)) {
        map[start.toLocalDate()] = java.util.ArrayList()
        start = start.plusWeeks(1)
      }
      map.keys.sortedBy { it }.forEach { logger.info("key is {}", it) }
      sortedSlots.forEach { map[it.date.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))]?.add(it) }

      map.keys.stream().sorted().map { processTable(it, map[it]) }.collect(Collectors.toList()).forEach { section = section.plus(it) }
    } else {
      section = section.plus(processTable(null, sortedSlots))
    }
    return section.plus(" </section>")
  }

  private fun processTable(monday: LocalDate?, rows: List<TimeSlot>?): String {

    var h2Value = "Upcoming Events - Dates to be confirmed"

    if (monday != null) {
      val df = DateTimeFormatter.ofPattern("EEEE, MMMM d")
      h2Value = if(monday.isBefore(LocalDate.now())) "Week that commenced ${monday.format(df)}" else "Week commencing ${monday.format(df)}"
    }
    var htmlTable = "          <header class=\"post-header\">\n" +
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


    return htmlTable.plus("</tbody>\n" +
      "              </table>\n" +
      "            </div>\n" +
      "            \n" +
      "          </header>")

  }








}
