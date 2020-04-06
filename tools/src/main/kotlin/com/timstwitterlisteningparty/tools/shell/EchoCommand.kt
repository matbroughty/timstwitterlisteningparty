package com.timstwitterlisteningparty.tools.shell

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class EchoCommand {

  @ShellMethod("Displays greeting message to the user whose name is supplied")
  fun echo(@ShellOption("-N", "--name") name : String) : String{
    return  "Hello $name! You are running spring shell cli-demo."
  }

}
