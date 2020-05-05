package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.parser.CollectionsHtmlPagesCreator
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


/**
 */
@ShellComponent
class CollectionPagesGenerateCommand {

  @ShellMethod("Regenerates the pages/list collections ")
  fun collections() : String{
    return CollectionsHtmlPagesCreator().createTwitterListPages(writeToFile = true).toString()
  }
}
