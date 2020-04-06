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

data class TimeSlot(val dateStr: String = "?",
                    val timeStr: String = "?",
                    @CsvBindByPosition(position = 1) val band: String = "",
                    @CsvBindByPosition(position = 2) val album: String = "",
                    @CsvBindByPosition(position = 3) val link: String = ""){

  private val logger = LoggerFactory.getLogger(javaClass)

  @CsvDate(value = "yyyy-MM-dd'T'HH:mm")
  @CsvBindByPosition(position = 0)
  private val _date: LocalDateTime? = buildDate(dateStr, timeStr)
  val date: LocalDateTime
    get() = this._date!!



  private fun buildDate(dateStr: String, timeStr: String) : LocalDateTime?{
    logger.info("Parsing date {} and time {} for band {} and album", dateStr, timeStr, band, album)
    // no date yet
    if(dateStr.contains("?")){
      return LocalDateTime.ofInstant(Instant.ofEpochMilli( 0L ), ZoneOffset.UTC)
    }

    val parseFormatter = DateTimeFormatterBuilder()
      .appendPattern("EEEE d['st']['nd']['rd']['th'] MMMM")
      .parseDefaulting(ChronoField.YEAR, 2020)
      .toFormatter(Locale.ENGLISH)
    val date = LocalDate.parse(dateStr, parseFormatter).withYear(2020)
    logger.info("Parsed date = {} ", date)
    val time = Integer.parseInt(StringUtils.substringBefore(timeStr, ":"))
    logger.info("Parsed time = {} ", time)
    return LocalDateTime.of(date, LocalTime.of(time + 12, 0))
  }


  override fun toString(): String {
    return "Date is $date for band $band and album $album - twitter link $link"
  }

  fun buildHtmlRow(): String {
    var hours = "?"
    var engTime = "?"
    var button = "pure-button-active"
    if(date.year != 1970){
      if (LocalDate.now().isEqual(date.toLocalDate())){
          button = "pure-button-primary"
        }
      hours = date.format(DateTimeFormatter.ofPattern("h:a"))
      engTime = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
    }
    return "                <tr>\n" +
      "                  <td>$engTime</td>\n" +
      "                  <td>$hours</td>\n" +
      "                  <td>$band</td>\n" +
      "                  <td>$album</td>\n" +
      "                  <td><a class=\"pure-button $button\"\n" +
      "                                     href=\"$link\"><i\n" +
      "                    class=\"fab fa-twitter-square\"></i></a></td>\n" +
      "                </tr>"

  }
}
