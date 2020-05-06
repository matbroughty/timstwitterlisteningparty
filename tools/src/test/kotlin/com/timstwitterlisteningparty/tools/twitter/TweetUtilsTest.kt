package com.timstwitterlisteningparty.tools.twitter

import com.timstwitterlisteningparty.tools.parser.Replay
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestTemplate


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
  }

}
