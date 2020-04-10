package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.FileCreator
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


@ShellComponent
class CsvToHtmlCommand(val fileCreator: FileCreator) {

  @ShellMethod("Produces the completed-time-slots.html, date-tbd-time-slots.html and the upcoming-time-slots.html files from a csv file - defaults to using time-slots-data.csv")
  fun html(): String {
    return "The html file was created ${fileCreator.createFiles()}"
  }
}

