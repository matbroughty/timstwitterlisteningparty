package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.CsvDate
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

/**
 * Represent a row in the time-slot-data.csv
 * Note - also used to parse the existing html in completed-time-slots.html,
 * date-tbd-time-slots.html and upcoming-time-slots.html and these have times as date and time string, hence constructor
 */
data class TimeSlot(val dateStr: String = "?",
                    val timeStr: String = "?",
                    @CsvBindByPosition(position = 0) @CsvDate(value = "yyyy-MM-dd'T'HH:mm")
                    var isoDate: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC),
                    @CsvBindByPosition(position = 1)
                    val band: String = "",
                    @CsvBindByPosition(position = 2)
                    val album: String = "",
                    @CsvBindByPosition(position = 3)
                    val link: String = "") : HtmlRow {

  private val logger = LoggerFactory.getLogger(javaClass)

  init {
    if (dateStr.isNotEmpty()) {
      // parsing html so work out date
      isoDate = buildDate(dateStr, timeStr)
    }


  }


  /**
   * If from html then we need to turn something like Tuesday, April 7 	9:pm into
   * a LocalDateTime
   * @param dateStr in form EEEE, MMMM d
   * @param timeStr form 9:pm
   */
  private fun buildDate(dateStr: String, timeStr: String): LocalDateTime {
    logger.info("Parsing date {} and time {} for band {} and album", dateStr, timeStr, band, album)
    // no date yet
    if (dateStr.contains("?")) {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC)
    }
    val parseFormatter = DateTimeFormatterBuilder()
      .appendPattern("EEEE, MMMM d")
      .parseDefaulting(ChronoField.YEAR, 2020)
      .toFormatter(Locale.ENGLISH)
    val date = LocalDate.parse(dateStr, parseFormatter)
    logger.debug("Parsed date = {} ", date)
    val time = Integer.parseInt(StringUtils.substringBefore(timeStr, ":"))
    logger.debug("Parsed time = {} ", time)
    return LocalDateTime.of(date, LocalTime.of(time + 12, 0))
  }


  /**
   * When converting back to an html row this will do the biz
   */
  override fun buildHtmlRow(): String {

    /**
     * Styling for normal link button, or active if today
     */
    var button = "pure-button-active"
    var hours = "?"
    var engTime = "?"


    // active button for today
    if (isoDate.year != 1970) {
      if (LocalDate.now().isEqual(isoDate.toLocalDate())) {
        button = "pure-button-primary"
      }
      // date and time strings displayed in the html
      engTime = isoDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
      hours = isoDate.format(DateTimeFormatter.ofPattern("h:mm a"))
    }

    /**
     * The row html to populate
     * @see buildHtmlRow
     */
     return  "                <tr>\n" +
      "                  <td>$engTime</td>\n" +
      "                  <td style=\"text-align:right\">$hours</td>\n" +
      "                  <td>$band</td>\n" +
      "                  <td>$album</td>\n" +
      "                  <td><a class=\"pure-button $button\"\n" +
      "                                     href=\"$link\"><i\n" +
      "                    class=\"fab fa-twitter-square\"></i></a></td>\n" +
      "                </tr>"

  }
}
