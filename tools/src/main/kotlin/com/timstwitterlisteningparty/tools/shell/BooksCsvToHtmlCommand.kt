package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.BookReviewFileCreator
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


@ShellComponent
class BooksCsvToHtmlCommand(val fileCreator: BookReviewFileCreator) {

  @ShellMethod("Produces the snippets/book-reviews-shops.html from the book-review-data.csv")
  fun bookshtml(): String {
    return "The html file was created ${fileCreator.createFiles(fileName = "book-review-data.csv")}"
  }
}

