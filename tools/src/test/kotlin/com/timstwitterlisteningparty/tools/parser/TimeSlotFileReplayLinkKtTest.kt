package com.timstwitterlisteningparty.tools.parser

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TimeSlotFileReplayLinkKtTest {

  @Test
  fun testGetListeningTweetList() {
    val replay = Replay(replayId = "94", band = "New Order", album = "Low-Life")
    assertEquals(replay.getListeningTweetList().size, 165)
  }

}
