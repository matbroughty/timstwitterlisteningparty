package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvBindByName
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
 * Note - also used to parse the existing html in completed-time-slots.html using the [FreeMarkerUtils] and
 * [UPCOMING_FTL] and why we have the suppressed unused
 */
@Suppress("unused")
data class TimeSlot(val dateStr: String = "?",
                    val timeStr: String = "?",
                    @CsvBindByName(column = "iso-date")
                    @CsvBindByPosition(position = 0) @CsvDate(value = "yyyy-MM-dd'T'HH:mm")
                    var isoDate: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC),
                    @CsvBindByName(column = "band")
                    @CsvBindByPosition(position = 1)
                    val band: String = "",
                    @CsvBindByName(column = "album")
                    @CsvBindByPosition(position = 2)
                    val album: String = "",
                    @CsvBindByName(column = "confirmation-tweet")
                    @CsvBindByPosition(position = 3)
                    val link: String = "",
                    @CsvBindByName(column = "replay-link")
                    @CsvBindByPosition(position = 4)
                    var replayLink: String = "",
                    @CsvBindByName(column = "tweeters")
                    @CsvBindByPosition(position = 5)
                    var tweeters: String = "",
                    @CsvBindByName(column = "curated-list")
                    @CsvBindByPosition(position = 6)
                    var twitterCollectionLink: String = "",
                    @CsvBindByName(column = "spotify-link")
                    @CsvBindByPosition(position = 7)
                    var spotifyLink: String = "", // link to album
                    @CsvBindByPosition(position = 8)
                    @CsvBindByName(column = "spotify-img-link")
                    var spotifyImgLink: String = "", // link to album artwork  300/300
                    @CsvBindByPosition(position = 9)
                    @CsvBindByName(column = "spotify-img-link-small")
                    var spotifyImgLinkSmall: String = "", // link to album artwork 60/60
                    @CsvBindByPosition(position = 10)
                    @CsvBindByName(column = "spotify-year")
                    var spotifyYear: String = "", // year album released
                    @CsvBindByPosition(position = 11)
                    @CsvBindByName(column = "listening-party-number")
                    var listeningPartyNumber: String = "", // twitter listening party number
                    @CsvBindByPosition(position = 12)
                    @CsvBindByName(column = "spotify-img-link-large")
                    var spotifyImgLinkLarge: String = "" // link to album artwork 640/640

) {

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
   * Create a hash of the band and album
   */
  fun hashBandAlbum(): Int {
    return band.trim().toLowerCase().plus(album.trim().toLowerCase()).hashCode()
  }


  fun getSpotifyImageLink(): String {
    return if (spotifyImgLink.isBlank()) {
      "img/blankcd.png"
    } else spotifyImgLink
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
  fun tweeterList(): MutableList<String> {
    if (tweeters.isEmpty()) {
      return Collections.emptyList()
    }
    return tweeters.split(":").map { it.trim() }.toMutableList()
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

  @Suppress("unused")
  fun isToday(): Boolean {
    return LocalDate.now().isEqual(isoDate.toLocalDate())
  }


  fun isAfter(date: LocalDateTime): Boolean {
    return isoDate.toLocalDate().isAfter(date.toLocalDate())
  }

  /**
   * If doesn't exist on spotify or some other link held in the [spotifyLink]
   * field then this method will tell us
   */
  fun isActuallySpotifyLink(): Boolean {
    return spotifyLink.contains("spotify")
  }

  /**
   * Is it not only a spotify link but a spotify album link as opposed to
   * an artist or single
   */
  private fun isActuallySpotifyAlbumLink(): Boolean {
    return isActuallySpotifyLink() && spotifyLink.contains("album")
  }

  /**
   * Get the spotify album link id - if it is
   */
  fun getSpotifyAlbumId(): String {
    return if (isActuallySpotifyAlbumLink()) spotifyLink.substringAfterLast("/") else ""
  }

  /**
   * Displays the isoDate in format or TBC if 1970
   * used in freemarker template
   */
  @Suppress("unused")
  fun dateDisplayString(): String {
    if (isoDate.year == 1970) {
      return "TBC"
    }
    // Tim asked to remove year
//    if(isNextYear()){
//      return isoDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d (yyyy)"))
//    }
    return isoDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
  }


  /**
   * formats in yyyy-MM-dd
   */
  fun dateOrderDisplay(): String {
    if (isoDate.year == 1970) {
      return "TBC"
    }
    return isoDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  }

  /**
   * Gets the archive collection full link from the replayId
   * used in freemarker template
   */
  @Suppress("unused")
  fun getCollectionLink(): String {
    return "https://timstwitterlisteningparty.com/pages/list/collection_${replayId()}.html"
  }

  @Suppress("unused")
  fun timeDisplayString(): String {
    return isoDate.format(DateTimeFormatter.ofPattern("h:mm"))
  }

  @Suppress("unused")
  fun amPmDisplayString(): String {
    return isoDate.format(DateTimeFormatter.ofPattern("a"))
  }

  /**
   * This should probably be done in the template...
   */
  @Suppress("unused")
  fun buildTweeterLinks(): String {
    var html = "<br/><small>"
    html = html.plus(tweeterLinkList().map { "<a class=\"text-muted\" target=\"_blank\" href=\"$it\">@${it.substringAfterLast("/")}</a>" }.toList())
    if (tweeterLinkList().isNotEmpty()) {
      // silent listening parties will have no tweeters so don't add listening party, otherwise do
      html = html.plus(", <a class=\"text-muted\" target=\"_blank\" href=\"https://twitter.com/LlSTENlNG_PARTY\">@LlSTENlNG_PARTY</a>")
    }
    return html.plus("</small>").replace("[", "").replace("]", "")
  }


  fun spotifyDateDisplay(): String {
    return if (spotifyYear.isEmpty()) "" else spotifyYear.substring(0, 4)
  }

  fun spotifyDateDisplayFull(): String {
    return if (spotifyYear.isEmpty() || spotifyYear.length < 10) ""
    else LocalDate.parse(spotifyYear, DateTimeFormatter.ISO_DATE).format(DateTimeFormatter.ofPattern("d MMMM yyyy"))
  }


  fun spotifyMonthDay(): MonthDay {
    val releaseDate = LocalDate.parse(spotifyYear, DateTimeFormatter.ISO_DATE)
    return MonthDay.of(releaseDate.month, releaseDate.dayOfMonth)
  }


  fun spotifyThisYear(): Boolean {
    if (spotifyYear.isNotBlank() && spotifyYear.length < 10) {
      return false
    }
    val releaseDate = LocalDate.parse(spotifyYear, DateTimeFormatter.ISO_DATE)
    return releaseDate.year == LocalDate.now().year
  }

  fun hasReplay(): Boolean {
    return replayLink.isNotBlank()
  }

  fun is1970(): Boolean {
    return isoDate.year == 1970
  }


  private fun isNextYear(): Boolean {
    return isoDate.year == LocalDateTime.now().year + 1
  }

  fun hasNumber(): Boolean {
    return listeningPartyNumber.isNotBlank() && listeningPartyNumber != "-1"
  }


  fun buildTweeters(lengthRemaining: Int): String {

    var charCount = lengthRemaining - (this.album.length + this.band.length)
    logger.info("remaining char count so far $charCount")

    val tweetList = tweeterList()
    // Only include Tim if his band
    if (!band.equals("Charlatans", true)
      && !band.equals("Tim Burgess", true)) {
      tweetList.remove("@Tim_Burgess")
    }

    // tweeting from listening party so no point including it unless it was one of the few where
    // the tweets came from the listening party account
    if(tweetList.size > 1) {
      tweetList.remove("@LlSTENlNG_PARTY")
    }

    val tweetString = tweetList.map {
      val tweetHandleLength = (it.length + 1)
      if (charCount - tweetHandleLength > 0) {
        charCount -= tweetHandleLength
        "$it "
      } else {
        ""
      }
    }.toList().joinToString(separator = "")

    logger.info("tweeters String = $tweetString")
    return tweetString;

  }


}
