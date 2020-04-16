package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.BookReviewFileCreator
import com.timstwitterlisteningparty.tools.parser.BookStoreFileCreator
import com.timstwitterlisteningparty.tools.parser.RecordStoreFileCreator
import com.timstwitterlisteningparty.tools.parser.TimeSlotFileCreator
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class AllHtmlCommand(val timeSlotFileCreator: TimeSlotFileCreator, val recordStoreFileCreator: RecordStoreFileCreator,
                     val reviewFileCreator: BookReviewFileCreator, val bookStoreFileCreator: BookStoreFileCreator) {

  @ShellMethod("Creates all of the html files from time-slot-data.csv, book-shops-data.csv, book-review-data.csv and record-store-data.csv")
  fun allhtml() : String{
    var returnStr = "The time slot html file was created ${timeSlotFileCreator.createFiles(fileName = "time-slot-data.csv")}"
    returnStr = returnStr.plus("The record slot html file was created ${recordStoreFileCreator.createFiles(fileName = "record-store-data.csv")}")
    returnStr = returnStr.plus("The book review slot html file was created ${reviewFileCreator.createFiles(fileName = "book-review-data.csv")}")
    returnStr = returnStr.plus("The book store html file was created ${bookStoreFileCreator.createFiles(fileName = "book-shops-data.csv")}")
    return  returnStr
  }

}
