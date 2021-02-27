package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.TimeSlot
import com.timstwitterlisteningparty.tools.parser.TimeSlotReader
import com.timstwitterlisteningparty.tools.social.TweetUtils
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class TweetCommand {

  @ShellMethod("tweets out the collection page for the replayId i.e. http://timstwitterlisteningparty.com/pages/list/collection_replayId.html")
  fun tweetCollection(@ShellOption("-R", "--id") replayId: String): String {
    val timeSlot = findTimeSlot(replayId)
    val msg: String = if (timeSlot != null) {
      TweetUtils().tweetCollection(timeSlot, replayId = replayId)
    } else {
      "couldn't find TimeSlot for replayId $replayId - no tweet sent"
    }
    return "tweet collection for $replayId = $msg"
  }

  @ShellMethod("tweets out the replay page for the replayId i.e. https://timstwitterlisteningparty.com/pages/replay/feed_replayId.html ")
  fun tweetReplay(@ShellOption("-R", "--id") replayId: String): String {

    val timeSlot = findTimeSlot(replayId)
    val msg: String
    msg = if (timeSlot != null) {
      TweetUtils().tweetReplay(timeSlot, timeSlot.replayLink)
    } else {
      "couldn't find TimeSlot for replayId $replayId - no tweet sent"
    }
    return "tweet replay for $replayId = $msg"
  }

  @ShellMethod("Builds new collections (chron and reverse chron) from the first tweet in every replay ")
  fun firstTweets(): String {
    return "tweet list oldest first collection = " +
      "${TweetUtils().ttlpFirstTweetCollection(collectionIdStr = "")} newest first collection = ${TweetUtils().ttlpFirstTweetCollection(collectionIdStr = "", order = "tweet_reverse_chron")}"
  }

  @ShellMethod("Tweet any anniversaries based on spotify alum date in the time-slot-data.csv")
  fun tweetAnniversary(@ShellOption("-L", "--log") logOnly: String): String {
    return "tweet anniversary - anything to tweet = " +
      "${TweetUtils().tweetAnniversary(logOnly = logOnly.toBoolean())}"
  }

  @ShellMethod("Tweet any yearly twiiter anniversaries based on iso date for listening party date in the time-slot-data.csv")
  fun tweetYearlyAnniversary(@ShellOption("-L", "--log") logOnly: String): String {
    return "tweet anniversary - anything to tweet = " +
      "${TweetUtils().tweetYearlyAnniversary(logOnly = logOnly.toBoolean())}"
  }


  private fun findTimeSlot(replayId: String): TimeSlot? {
    val beans = TimeSlotReader().timeSlots
    return beans.find { it.replayId().trim() == replayId.trim() }
  }

}
