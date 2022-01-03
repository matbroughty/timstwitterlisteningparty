package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.TimeSlotToJson
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


/**
 */
@ShellComponent
class TimeSlotJsonGenerateCommand {

  @ShellMethod("Regenerates the timeslot as a json file")
  fun json(): Int {
    return TimeSlotToJson().createFiles(fileName = "data/time-slot-data.csv", writeToFile = true)
  }
}
