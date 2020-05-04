package com.timstwitterlisteningparty.tools.shell

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod


/**
 * Calls the 'replay' and then 'allhtml' commands.
 * <br>
 * Effectively regenerates the html snippets via the [AllHtmlCommand.allhtml] and before that
 * updates the data/time-slot-data.csv with any replay links via the [AddReplayToCsvCommand.timeSlotFileReplayLink]
 */
@ShellComponent
class RegenerateCommand(val allHtmlCommand: AllHtmlCommand, val addReplayToCsvCommand: AddReplayToCsvCommand) {

  @ShellMethod("Regenerates the site calling the command 'replay' and then 'allhtml' commands")
  fun regen() : String{
    val allHtml = allHtmlCommand.allhtml()
    return "regen ${addReplayToCsvCommand.replay()} and allHtml length ${allHtml.length}"
  }
}
