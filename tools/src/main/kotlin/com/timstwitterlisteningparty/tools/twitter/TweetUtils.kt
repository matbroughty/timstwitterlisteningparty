package com.timstwitterlisteningparty.tools.twitter

import com.timstwitterlisteningparty.tools.parser.Replay
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.HttpParameter
import twitter4j.JSONObject
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder


@Component
class TweetUtils() {


  private val logger = LoggerFactory.getLogger(javaClass)


  fun getTwitter() : Twitter?{
    var twitter: Twitter? = null
    try {
      val consumerKey : String? = System.getenv("twitter4j_oauth_consumerKey")
      val consumerSecret : String?  = System.getenv("twitter4j_oauth_consumerSecret")
      val accessToken : String?  = System.getenv("twitter4j_oauth_accessToken")
      val accessTokenSecret: String?  = System.getenv("twitter4j_oauth_accessTokenSecret")

      val cb = if (consumerKey.isNullOrEmpty()) {
        // in the properties file
        ConfigurationBuilder()
      } else {
        ConfigurationBuilder()
          .setOAuthConsumerKey(consumerKey)
          .setOAuthConsumerSecret(consumerSecret)
          .setOAuthAccessToken(accessToken)
          .setOAuthAccessTokenSecret(accessTokenSecret)
      }
      val tf = TwitterFactory(cb.build())
      twitter = tf.instance
    } catch (e: Exception) {
      logger.info("Some badness with getting twitter instance ${e.localizedMessage}", e)
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

  fun createCollection(replay: Replay?): String {
    if(replay == null){
      return "no replay to create collection from"
    }
    var retMsg = ""
    try {
      val response = getTwitter()?.postResponse("https://api.twitter.com/1.1/collections/create.json",
        HttpParameter("name", replay.getCollectionName()),
        HttpParameter("description", replay.getCollectionDesc()),
        HttpParameter("timeline_order", "tweet_chron")
      )
      logger.info("response from collection  is $response")
      if (response != null && response.statusCode == 200) {
        val collectionId = (response?.asJSONObject().get("response") as JSONObject).get("timeline_id").toString()
        retMsg = retMsg.plus("https://twitter.com/LlSTENlNG_PARTY/timelines/${collectionId.substringAfter("custom-")}")
        logger.info(retMsg)
        replay.getListeningTweetList().forEach {
          logger.info("adding tweet $it to collection id $collectionId")
          getTwitter()?.postResponse("https://api.twitter.com/1.1/collections/entries/add.json",
            HttpParameter("tweet_id", it),
            HttpParameter("id", collectionId)
          )
        }
      }
    }catch( e : Exception){
      logger.info("Some badness with createCollection on twitter  ${e.localizedMessage}", e)
    }
    return retMsg
  }

}
