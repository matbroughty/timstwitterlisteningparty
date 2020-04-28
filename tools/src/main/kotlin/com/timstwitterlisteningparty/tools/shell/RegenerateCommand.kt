package com.timstwitterlisteningparty.tools.shell

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class RegenerateCommand(val allHtmlCommand: AllHtmlCommand, val addReplayToCsvCommand: AddReplayToCsvCommand) {

  @ShellMethod("Regenerates the site calling the command 'replay' and then 'allhtml' commands")
  fun regen() : String{
    return "regen ${addReplayToCsvCommand.replay()} and allHtml ${allHtmlCommand.allhtml()}"
  }
}
