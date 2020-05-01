package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvBindByPosition

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


}
