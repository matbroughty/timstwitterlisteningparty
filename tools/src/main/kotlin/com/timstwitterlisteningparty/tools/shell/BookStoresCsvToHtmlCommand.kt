package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.BookStoreFileCreator
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


@ShellComponent
class BookStoresCsvToHtmlCommand(val fileCreator: BookStoreFileCreator) {

  @ShellMethod("Produces the snippets/book-shops.html from the book-shops-data.csv")
  fun bookshopshtml(): String {
    return "The book store html file was created ${fileCreator.createFiles(fileName = "book-shops-data.csv")}"
  }
}

