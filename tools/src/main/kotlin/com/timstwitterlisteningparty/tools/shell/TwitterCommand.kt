package com.timstwitterlisteningparty.tools.shell

import com.timstwitterlisteningparty.tools.twitter.TweetUtils
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class TwitterCommand(val tweetUtils: TweetUtils) {

  @ShellMethod("Sends a message from @timslisteningp1 ")
  fun tweet(@ShellOption("-T", "--tweet") name: String): String {
    return "Tweeted ${tweetUtils.createCollection(name)}! $name."
  }
}
