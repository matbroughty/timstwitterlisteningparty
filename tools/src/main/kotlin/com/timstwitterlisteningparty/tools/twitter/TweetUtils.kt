package com.timstwitterlisteningparty.tools.twitter

import com.fasterxml.jackson.databind.ObjectMapper
import com.timstwitterlisteningparty.tools.parser.Replay
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import com.timstwitterlisteningparty.tools.twitter.collections.Collections
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder


@Component
class TweetUtils() {


  private val logger = LoggerFactory.getLogger(javaClass)


  fun getTwitter() : Twitter?{
    var twitter: Twitter ?= null
    try {
      val cb = ConfigurationBuilder()
      val tf = TwitterFactory(cb.build())
       twitter = tf.instance
    } catch (e: Exception) {
      print("Some badness with creating twitter ${e.localizedMessage}")
    }
    return twitter

  }

  fun tweet(msg: String): String {
    return try {
      getTwitter()?.updateStatus(msg).toString()
    } catch (e: Exception) {
      print("Some badness with sending $msg  as a tweet ${e.localizedMessage}")
      e.localizedMessage
    }
  }


  fun tweetReplay(timeSlot: TimeSlot, replayLink: String): String {
    if(timeSlot.tweeterList().isEmpty()){
      return "no band/artist to tweet replay"
    }
    return tweet("Replay available ${timeSlot.tweeterList().first()} : ${timeSlot.band} : ${timeSlot.album} at $replayLink #TimsTwitterListeningParty")
  }

  /**
   *
  "id" => "custom-821991848667287552",
  "changes" => [
  [ "op" => "add", "tweet_id" => "821127339513823232" ] ,
  [ "op" => "add", "tweet_id" => "821122002253586432" ] ,
  [ "op" => "remove", "tweet_id" => "821127416013688832" ] ,
  [ "op" => "remove", "tweet_id" => "821127416013688832" ]
   ]
   */
  fun createCollection(replay: Replay?): String {

    if(replay == null){
      return "no replay to create collection from"
    }

    val collectionCreate = "https://api.twitter.com/1.1/collections/create.json?name=${replay.getCollectionName()}&description=${replay.getCollectionName()}&timeline_order=tweet_chron"

    val response = getTwitter()?.postResponse(collectionCreate)

    logger.info("response from collection  is $response")


    if(response != null && response.statusCode == 200) {
      //val objectMapper = ObjectMapper().readValue<Collections>(response.asString())
      replay.getListeningTweetList().chunked(100).forEach {

      }


    }

//    getTwitter().pu
//    val statusResponse = twitter.getResponse("https://api.twitter.com/1.1/collections/show.json?id=custom-1255049376134823938")
//
//    logger.info("http response $statusResponse")
    return "" //statusResponse.asString()

  }

}
