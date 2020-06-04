package com.timstwitterlisteningparty.tools.social

import com.timstwitterlisteningparty.tools.parser.Replay
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.HttpParameter
import twitter4j.JSONObject
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder


@Component
class TweetUtils {


  private val logger = LoggerFactory.getLogger(javaClass)


  fun getTwitter(): Twitter? {
    var twitter: Twitter? = null
    try {
      val consumerKey: String? = System.getenv("twitter4j_oauth_consumerKey")
      val consumerSecret: String? = System.getenv("twitter4j_oauth_consumerSecret")
      val accessToken: String? = System.getenv("twitter4j_oauth_accessToken")
      val accessTokenSecret: String? = System.getenv("twitter4j_oauth_accessTokenSecret")

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
    if (timeSlot.tweeterList().isEmpty()) {
      return "no band/artist to tweet replay"
    }
    if (!pageExists(replayLink)) {
      return "$replayLink page doesn't exist yet"
    }
    return tweet("Replay available ${timeSlot.tweeterList().first()} : ${timeSlot.band} : ${timeSlot.album} at $replayLink #TimsTwitterListeningParty")
  }

  fun pageExists(link: String): Boolean {
    try {
      return Jsoup.connect(link).get().text().isNotEmpty()
    } catch (e: Exception) {
      logger.warn("issue trying to get $link ${e.localizedMessage}")
    }
    return false

  }


  fun tweetCollection(timeSlot: TimeSlot, replayId: String): String {
    if (timeSlot.tweeterList().isEmpty() || replayId.isEmpty()) {
      return "no band/artist to tweet collection"
    }
    val curatedTweetUrl = "https://timstwitterlisteningparty.com/pages/list/collection_${replayId}.html"
    if (!pageExists(curatedTweetUrl)) {
      return "$curatedTweetUrl collection page doesn't exist yet"
    }
    return tweet("List available ${timeSlot.tweeterList().first()} : ${timeSlot.band} : ${timeSlot.album} at $curatedTweetUrl #TimsTwitterListeningParty")
  }

  fun createCollection(replay: Replay?): String {
    if (replay == null) {
      return "no replay to create collection from"
    }
    var retMsg = ""
    try {
      var response = getTwitter()?.postResponse("https://api.twitter.com/1.1/collections/create.json",
        HttpParameter("name", replay.getCollectionName()),
        HttpParameter("description", replay.getCollectionDesc()),
        HttpParameter("timeline_order", "tweet_chron")
      )
      logger.info("response from collection create is $response")
      if (response != null && response.statusCode == 200) {
        val collectionId = (response.asJSONObject().get("response") as JSONObject).get("timeline_id").toString()
        retMsg = retMsg.plus("https://twitter.com/LlSTENlNG_PARTY/timelines/${collectionId.substringAfter("custom-")}")
        logger.info(retMsg)
        replay.getListeningTweetList().chunked(100).forEach {
          addToCollection(it, collectionId)
        }

      }
    } catch (e: Exception) {
      logger.info("Some badness with createCollection on twitter  ${e.localizedMessage}", e)
    }
    return retMsg
  }

  /**
   * Builds up collection - already created collection so adds to it
   */
  fun ttlpFirstTweetCollection(collectionId: String = "1268620253954740224", replayIdStr: String = "1"): String {

    var retMsg = ""
    try {
      var replayId = replayIdStr.toInt()
      var replayFeedHtml = Jsoup.connect("https://timstwitterlisteningparty.com/snippets/replay/feed_${replayId}_snippet.html").get()
      val tweetList = ArrayList<String>()
      while (replayFeedHtml != null) {
        val replay = Replay(replayId = replayId.toString())
        val tweetId = replay.getFirstListeningTweet()
        if (tweetId.isNotBlank()) {
          tweetList.add(tweetId)
        }
        logger.info("added tweetid $tweetId for first tweet collection")
        replayId++

        replayFeedHtml = try {
          Jsoup.connect("https://timstwitterlisteningparty.com/snippets/replay/feed_${replayId}_snippet.html").get()
        } catch (e: Exception) {
          null;
        }
      }

      retMsg = retMsg.plus("https://twitter.com/LlSTENlNG_PARTY/timelines/$collectionId")
      logger.info("collection id for first tweet list is $collectionId and return message is  $retMsg")
      tweetList.chunked(100).forEach {
        addToCollection(it, collectionId)
      }
    } catch (e: Exception) {
      logger.info("Some badness with createCollection on twitter  ${e.localizedMessage}", e)
    }

    return retMsg

  }


  fun addToCollection(tweetIds: List<String>, collectionId: String) {

    var json = "{\"id\": \"$collectionId\",\"changes\": ["
    json = json.plus(tweetIds.joinToString { tweetId -> "{ \"op\": \"add\", \"tweet_id\": \"$tweetId\"}" })
    json = json.plus("]}")
    logger.info("json curation json is $json")
    val response = getTwitter()?.postResponse("https://api.twitter.com/1.1/collections/entries/curate.json", JSONObject(json))
    logger.info("response from curate for collection $collectionId is $response")
  }


}
