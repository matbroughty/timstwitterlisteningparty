package com.timstwitterlisteningparty.tools.shell

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

@ShellComponent
class TwitterCollectionCommand {

  @ShellMethod("Performs some twitter stuff using ttlp")
  fun twitter() : String {
    return "twitter completed ${twittery()}"

  }
  fun twittery() : String{

    val cb = ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey("")
      .setOAuthConsumerSecret("")
      .setOAuthAccessToken("212513926-")
      .setOAuthAccessTokenSecret("")
    val tf = TwitterFactory(cb.build())
    val twitter: Twitter = tf.instance

    //val status = twitter.tweets().updateStatus("hello world")
    val status = twitter.updateStatus("hello world")
    twitter.oAuthAccessToken

    twitter.oAuthAccessToken

    return "status = ${status.text}"
  }

}
