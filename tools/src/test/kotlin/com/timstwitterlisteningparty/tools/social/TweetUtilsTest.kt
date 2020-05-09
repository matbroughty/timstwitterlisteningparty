package com.timstwitterlisteningparty.tools.social

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


internal class TweetUtilsTest {

//    @Test
//    fun createCollection() {
//      TweetUtils().createCollection(Replay(replayId = "34", band = "Cornershop", album = "England is a Garden", date = "30th March 2020"))
//    }


  @Test
  fun testPageExists(){
    assertFalse(TweetUtils().pageExists("www.bbc.co.uk"))
    assertTrue(TweetUtils().pageExists("https://bbc.co.uk"))
    assertTrue(TweetUtils().pageExists("https://timstwitterlisteningparty.com"))
    assertFalse(TweetUtils().pageExists("https://timstwitterlisteningparty.com/whatthe.html"))
    assertTrue(TweetUtils().pageExists("http://timstwitterlisteningparty.com/pages/list/collection_35.html"))
    assertFalse(TweetUtils().pageExists("http://timstwitterlisteningparty.com/pages/list/collection_35908.html"))
    assertTrue(TweetUtils().pageExists("https://timstwitterlisteningparty.com/pages/replay/feed_112.html"))
  }

}
