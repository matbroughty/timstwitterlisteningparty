package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.RecordStoreFileCreator
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


@ShellComponent
class RecordStoreCsvToHtmlCommand(val fileCreator: RecordStoreFileCreator) {

  @ShellMethod("Produces the record-stores.html")
  fun storesHtml(): String {
    return "The html file was created ${fileCreator.createFiles(fileName = "record-store-data.csv")}"
  }
}

