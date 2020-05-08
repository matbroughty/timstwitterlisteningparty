package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.BookReviewFileCreator
import com.timstwitterlisteningparty.tools.parser.BookStoreFileCreator
import com.timstwitterlisteningparty.tools.parser.RecordStoreFileCreator
import com.timstwitterlisteningparty.tools.parser.TimeSlotFileCreator
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class AllHtmlCommand(val timeSlotFileCreator: TimeSlotFileCreator, val recordStoreFileCreator: RecordStoreFileCreator,
                     val reviewFileCreator: BookReviewFileCreator, val bookStoreFileCreator: BookStoreFileCreator) {

  @ShellMethod("Creates all of the html files from data/time-slot-data.csv, data/book-shops-data.csv, data/book-review-data.csv and data/record-store-data.csv")
  fun allhtml(): String {
    var returnStr = "The time slot html file was created ${timeSlotFileCreator.createFiles(fileName = "data/time-slot-data.csv")}"
    returnStr = returnStr.plus("The record slot html file was created ${recordStoreFileCreator.createFiles(fileName = "data/record-store-data.csv")}")
    returnStr = returnStr.plus("The book review slot html file was created ${reviewFileCreator.createFiles(fileName = "data/book-review-data.csv")}")
    returnStr = returnStr.plus("The book store html file was created ${bookStoreFileCreator.createFiles(fileName = "data/book-shops-data.csv")}")
    return returnStr
  }

}
