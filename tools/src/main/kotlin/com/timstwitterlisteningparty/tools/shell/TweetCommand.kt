package com.timstwitterlisteningparty.tools.shell

import com.opencsv.bean.CsvToBeanBuilder
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import com.timstwitterlisteningparty.tools.twitter.TweetUtils
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import java.io.FileReader

@ShellComponent
class TweetCommand {

  @ShellMethod("tweets out the collection page for the replayId i.e. http://timstwitterlisteningparty.com/pages/list/collection_replayId.html")
  fun tweetCollection(@ShellOption("-R", "--id") replayId : String) : String{
    val timeSlot = findTimeSlot(replayId)
    val msg: String
    msg = if(timeSlot != null) {
      TweetUtils().tweetCollection(timeSlot, replayId = replayId)
    }else{
      "couldn't find TimeSlot for replayId $replayId - no tweet sent"
    }
    return  "tweet collection for $replayId = $msg"
  }

  @ShellMethod("tweets out the replay page for the replayId i.e. https://timstwitterlisteningparty.com/pages/replay/feed_replayId.html ")
  fun tweetReplay(@ShellOption("-R", "--id") replayId : String) : String{

    val timeSlot = findTimeSlot(replayId)
    val msg: String
    msg = if(timeSlot != null) {
      TweetUtils().tweetCollection(timeSlot, replayId = replayId)
    }else{
      "couldn't find TimeSlot for replayId $replayId - no tweet sent"
    }
    return  "tweet replay for $replayId = $msg"
  }

  private fun findTimeSlot(replayId: String): TimeSlot? {
    val csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> =
      CsvToBeanBuilder<TimeSlot>(FileReader("data/time-slot-data.csv"))
    val beans: List<TimeSlot> = csvToBeanBuilder.withType(TimeSlot::class.java)
      .withIgnoreEmptyLine(true).build().parse()
    return beans.find { it.replayId().trim() == replayId.trim() }
  }

}
