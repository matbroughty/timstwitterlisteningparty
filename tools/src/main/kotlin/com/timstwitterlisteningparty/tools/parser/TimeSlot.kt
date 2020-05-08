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
 * Represent a row in the data/time-slot-data.csv
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
                    val link: String = "",
                    @CsvBindByPosition(position = 4)
                    var replayLink: String = "",
                    @CsvBindByPosition(position = 5)
                    var tweeters: String = "",
                    @CsvBindByPosition(position = 6)
                    var twitterCollectionLink: String = "") : HtmlRow {

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
    logger.debug("Parsing date {} and time {} for band {} and album", dateStr, timeStr, band, album)
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
    // english date format - i.e. April 13th
    var engDate = "?"
    var twitterIcon = if (replayLink.isBlank()) {
      "fab fa-twitter-square"
    } else "fas fa-redo"


    // active button for today
    if (isoDate.year != 1970) {
      if (LocalDate.now().isEqual(isoDate.toLocalDate())) {
        twitterIcon = "fab fa-twitter"
      }
      // date and time strings displayed in the html
      engDate = isoDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
      // if time is 00 but date isn't 1970 then we know the day but not the time so set to TBC
      if (isoDate.hour != 0) {
        hours = isoDate.format(DateTimeFormatter.ofPattern("h:mm a"))
      } else {
        hours = "TBC"
      }
    }

    /**
     * The row html to populate
     * @see buildHtmlRow
     */
    return "                <tr>\n" +
      "                  <td>$engDate</td>\n" +
      "                  <td style=\"text-align:right\">$hours</td>\n" +
      "                  <td>$band</td>\n" +
      "                  <td>$album</td>\n" +
      "                  <td><a class=\"pure-button $button\"\n" +
      "                                     href=\"${if (replayLink.isEmpty()) {
        link
      } else replayLink} \"><i\n" +
      "                    class=\"$twitterIcon\"></i></a></td>\n" +
      "                </tr>"

  }

  /**
   * When converting back to an html row this will do the biz
   */
  fun buildHtmlCardBody(): String {

    /**
     * Styling for normal link button, or active if today
     */
    var button = "pure-button-active"
    var twitterIcon = "fa-twitter-square"
    var hours = "?"
    var amPm = ""
    val isNow = LocalDate.now().isEqual(isoDate.toLocalDate())
    var twitterIds = ""
    // active button for today
    if (isoDate.year != 1970) {
      if (isNow) {
        twitterIcon = "fa-twitter"
        if (tweeters.isNotEmpty()) {
          twitterIds = buildTweeterLinks()
        }
      }
      if (isoDate.hour != 0) {
        hours = isoDate.format(DateTimeFormatter.ofPattern("h:mm"))
        amPm = isoDate.format(DateTimeFormatter.ofPattern("a"))
      } else {
        hours = "TBC"
      }
    }

    /**
     * The row html to populate
     */
    return "        <div class=\"card-body\">\n" +
      "          <table style=\"width: 100%;\">\n" +
      "            <tr>\n" +
      "              <td width=\"25%\" class=\"font-weight-light\" style=\"text-align:left;\">\n" +
      "                $hours<sup> $amPm</sup>\n" +
      "              </td>\n" +
      "              <td width=\"60%\" style=\"text-align:left;\">\n" +
      "                <b>$band</b><br/>$album\n $twitterIds" +
      "              </td>\n" +
      "              <td width=\"15%\"><a class=\"pure-button $button\"\n" +
      "                                 href=\"$link\" target=\"_blank\" ><i\n" +
      "                class=\"fab $twitterIcon\"></i></a></td>\n" +
      "            </tr>\n" +
      "          </table>\n" +
      "        </div>"

  }

  private fun buildTweeterLinks(): String {
    var html = "<br/><small>"
    html = html.plus(tweeterLinkList().map { "<a class=\"text-muted\" target=\"_blank\" href=\"$it\">@${it.substringAfterLast("/")}</a>" }.toList())
    return html.plus("</small>").replace("[", "").replace("]", "")
  }

  /**
   * Create a hash of the band and album
   */
  fun hashBandAlbum(): Int {
    return band.trim().toLowerCase().plus(album.trim().toLowerCase()).hashCode()
  }


  /**
   * Get a list from the ":" separated list of tweeters
   * and turn into a proper twitter link list
   */
  fun tweeterLinkList(): List<String> {
    if (tweeters.isEmpty()) {
      return Collections.emptyList()
    }
    return tweeters.split(":").map { "https://twitter.com/${it.replace("@", "").trim()}" }
  }

  /**
   * Just a list of the @name twitter handle
   * @see tweeterLinkList for full link
   */
  fun tweeterList(): List<String> {
    if (tweeters.isEmpty()) {
      return Collections.emptyList()
    }
    return tweeters.split(":").map { it.trim() }
  }


  private fun isEmpty(): Boolean {
    return band.isEmpty()
  }


  fun replayId(): String {
    if (replayLink.isNotEmpty()) {
      return StringUtils.substringBefore(replayLink.substringAfterLast("feed_"), ".html")
    }
    return ""
  }

  /**
   * If we have a replay link then we have the tweets to create a collection but
   * check we haven't already generated it
   */
  fun requiresTwitterCollection(): Boolean {
    return !isEmpty() && replayLink.isNotEmpty() && twitterCollectionLink.isEmpty()

  }

}
