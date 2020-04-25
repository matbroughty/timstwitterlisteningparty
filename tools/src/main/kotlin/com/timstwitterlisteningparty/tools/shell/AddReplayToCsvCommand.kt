package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.TimeSlotFileReplayLink
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class AddReplayToCsvCommand(val timeSlotFileReplayLink: TimeSlotFileReplayLink) {

  @ShellMethod("Add a column (5th) to the data/time-slot.date.csv file from the url view-source: https://timstwitterlisteningparty.com/snippets/replay/feed_index_snippet.html")
  fun replay(): String {
    return "The generated-time-slot.date.csv file was created: ${updateFile()}"
  }


  fun updateFile() :String {
    return  timeSlotFileReplayLink.addReplayLink()
  }

}
