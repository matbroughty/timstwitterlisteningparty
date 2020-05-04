package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvBindByPosition
import org.jsoup.Jsoup

/**
 * Reads the replay info from http://www.sk7software.co.uk/listeningparty/scripts/listParties.php
 */
data class Replay(@CsvBindByPosition(position = 0)
                  var replayId: String = "",
                  @CsvBindByPosition(position = 1)
                  val band: String = "",
                  @CsvBindByPosition(position = 2)
                  val album: String = "",
                  @CsvBindByPosition(position = 3)
                  val date: String = "",
                  @CsvBindByPosition(position = 4)
                  var twitterIds: String = ""){


  val trimmedId: String
    get() = replayId.trim()


  /**
   * Create a hash of the band and album
   */
  fun hashBandAlbum() : Int{
    return band.trim().toLowerCase().plus(album.trim().toLowerCase()).hashCode()
  }

  fun isEmpty() : Boolean{
    return band.isEmpty()
  }

  fun fullReplayLink(): String {
    if (trimmedId.isBlank()) {
      return ""
    }
    return "https://timstwitterlisteningparty.com/pages/replay/feed_$trimmedId.html"
  }

  fun getCollectionDesc() : String{
    return "$band : Album : $album listening party on $date #TimsTwitterListeningParty"
  }

  fun getCollectionName() : String{
    return album.take(25) // shouldn't be longer than 25...
  }

  /**
   * Returns tweet id's from the replay link page
   */
  fun getListeningTweetList(): List<String> {
    val replayFeedHtml = Jsoup.connect("https://timstwitterlisteningparty.com/snippets/replay/feed_${trimmedId}_snippet.html").get()
    return replayFeedHtml.select("div[id^=tweet-feed-]").map { it.attr("data-url").toString().substringAfterLast("%2F") }.filter { it.isNotEmpty() }.toList()
  }


}
