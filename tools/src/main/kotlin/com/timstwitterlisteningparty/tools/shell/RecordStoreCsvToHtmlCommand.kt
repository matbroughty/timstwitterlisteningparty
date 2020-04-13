package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.RecordStoreFileCreator
import com.timstwitterlisteningparty.tools.parser.TimeSlotFileCreator
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


@ShellComponent
class RecordStoreCsvToHtmlCommand(val fileCreator: RecordStoreFileCreator) {

  @ShellMethod("Produces the record-stores.html")
  fun storeshtml(): String {
    return "The html file was created ${fileCreator.createFiles()}"
  }
}

