package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.TimeSlotFileEnrich
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class CsvEnrichCommand(val timeSlotFileEnrich: TimeSlotFileEnrich) {

  @ShellMethod("Adds the replay link and spotify links to  data/time-slot.date.csv file from " +
    "the listParties.php")
  fun replay(): String {
    return "The updated time-slot.date.csv file was created\n: ${updateFile()}"
  }


  fun updateFile(): String {
    return timeSlotFileEnrich.enrich(writeToFile = true)
  }

}
