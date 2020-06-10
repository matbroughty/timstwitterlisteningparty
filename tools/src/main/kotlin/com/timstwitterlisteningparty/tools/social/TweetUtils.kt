package com.timstwitterlisteningparty.tools.social

import com.timstwitterlisteningparty.tools.parser.Replay
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import com.timstwitterlisteningparty.tools.parser.TimeSlotReader
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.*
import twitter4j.conf.ConfigurationBuilder
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter


@Component
class TweetUtils {


  private val logger = LoggerFactory.getLogger(javaClass)


  /**
   * Use env variables to get the Twitter client initialised for api calls
   */
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

  /**
   * Tweet a message
   */
  fun tweet(msg: String): String {
    return try {
      getTwitter()?.updateStatus(msg).toString()
    } catch (e: Exception) {
      print("Some badness with sending $msg  as a tweet ${e.localizedMessage}")
      e.localizedMessage
    }
  }

  /**
   * Runs through the ([TimeSlotReader#timeSlots] of timeSlots is empty) and if today is an anniversary tweets it
   * or logs it only if logOnly = true
   */
  fun tweetAnniversary(timeSlots: List<TimeSlot> = emptyList(), logOnly: Boolean = false): Boolean {
    val now = MonthDay.from(LocalDate.now())
    var anniversaryToTweet = false
    val timeSlotList = if (timeSlots.isEmpty()) TimeSlotReader().timeSlots else timeSlots
    timeSlotList
      .filter { it.spotifyYear.length == 10 }
      .filter { !it.spotifyThisYear() }
      .filter { it.tweeters.isNotEmpty() }
      .filter {
        val releaseDate = LocalDate.parse(it.spotifyYear, DateTimeFormatter.ISO_DATE)
        now == MonthDay.of(releaseDate.month, releaseDate.dayOfMonth)
      }
      .forEach {
        logger.info("found an anniversary for $it")
        anniversaryToTweet = true
        val releaseDate = LocalDate.parse(it.spotifyYear, DateTimeFormatter.ISO_DATE)
        val msg = "${releaseDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))}. " +
          if (it.replayLink.isEmpty()) {
            "${it.band} released ${it.album}. ${it.tweeterList().first()} will be hosting an upcoming listening party. ${it.link} #TimsTwitterListeningParty"
          } else {
            "${it.band} released ${it.album}. You can replay the ${it.tweeterList().first()} listening party here ${it.replayLink} #TimsTwitterListeningParty"
          }
        if (logOnly) {
          logger.info(msg)
        } else {
          tweet(msg)
        }
      }
    return anniversaryToTweet
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

  /**
   * For any given url returns true if it exists and we can read from it
   */
  fun pageExists(link: String): Boolean {
    try {
      return Jsoup.connect(link).get().text().isNotEmpty()
    } catch (e: Exception) {
      logger.warn("issue trying to get $link ${e.localizedMessage}")
    }
    return false

  }

  /**
   * Not much used method to tweet about a collection for a particular replay
   */
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

  /**
   * Creates a collection of tweets for the Replay feed id
   */
  fun createCollection(replay: Replay?): String {
    if (replay == null) {
      return "no replay to create collection from"
    }
    var retMsg = ""
    try {
      val collectionId = createCollection(replay.getCollectionName(), replay.getCollectionDesc())
      retMsg = retMsg.plus("https://twitter.com/LlSTENlNG_PARTY/timelines/${collectionId.substringAfter("custom-")}")
      logger.info("create collection for $replay is $retMsg")
      replay.getListeningTweetList().chunked(100).forEach {
        addToCollection(it, collectionId)
      }
    } catch (e: Exception) {
      logger.info("Some badness with createCollection on twitter  ${e.localizedMessage}", e)
    }
    return retMsg
  }

  /**
   * Builds up collection of the first tweet from the replay feeds (https://timstwitterlisteningparty.com/snippets/replay/feed_nn)
   * and (if collectionId is empty) adds to new collection or adds to existing collection.  Pass in the replayIdStr
   * and then only that number and highers replay first tweet will be added - useful so we don't have to create a new
   * collection each day
   */
  fun ttlpFirstTweetCollection(collectionIdStr: String = "custom-1268620253954740224", replayIdStr: String = "1", order: String = "tweet_chron"): String {

    var retMsg = ""
    var collectionId = collectionIdStr
    try {
      if (collectionId.isEmpty()) {
        collectionId = createCollection("Drop The Needle", "First Tweet: Every Replay:  ${if (order.equals("tweet_chron")) " Oldest " else " Most Recent "} First", order)
      }
      var replayId = replayIdStr.toInt()
      var replayFeedHtml = Jsoup.connect("https://timstwitterlisteningparty.com/snippets/replay/feed_${replayId}_snippet.html").get()
      val tweetList = ArrayList<String>()
      while (replayFeedHtml != null) {
        val replay = Replay(replayId = replayId.toString())
        val tweetId = replay.getFirstListeningTweet()
        if (tweetId.isNotBlank()) {
          tweetList.add(tweetId)
        }
        logger.info("added tweetid $tweetId for adding to first tweet collection")
        replayId++
        replayFeedHtml = try {
          Jsoup.connect("https://timstwitterlisteningparty.com/snippets/replay/feed_${replayId}_snippet.html").get()
        } catch (e: Exception) {
          // will indicate end of loop if no feed existed for feed number
          null
        }
      }
      retMsg = retMsg.plus("https://twitter.com/LlSTENlNG_PARTY/timelines/${collectionId.substringAfter("custom-")}")
      logger.info("collection id for first tweet list is $collectionId and return message is  $retMsg")
      tweetList.chunked(100).forEach {
        addToCollection(it, collectionId)
      }
    } catch (e: Exception) {
      logger.info("Some badness with ttlpFirstTweetCollection on twitter  ${e.localizedMessage}", e)
    }

    return retMsg

  }


  /**
   * Create a Twitter collection and returns the collection id in form "custom-$collectionId"
   * @param order defaults to tweet_chron i.e. oldest first, tweet_reverse_chron is newest first
   */
  fun createCollection(name: String, description: String, order: String = "tweet_chron"): String {
    val response = getTwitter()?.postResponse("https://api.twitter.com/1.1/collections/create.json",
      HttpParameter("name", name),
      HttpParameter("description", description),
      HttpParameter("timeline_order", order))
    logger.info("response from collection create $name is $response")
    if (response != null && response.statusCode == 200) {
      return (response.asJSONObject().get("response") as JSONObject).get("timeline_id").toString()
    }
    logger.warn("issue creating collection for $name and $description - collectionid not known")
    return ""
  }


  /**
   * The tweetIds to add to the collectionId (not collectionid needs to be in format "custom-$collectionId")
   */
  fun addToCollection(tweetIds: List<String>, collectionId: String) {
    var json = "{\"id\": \"$collectionId\",\"changes\": ["
    json = json.plus(tweetIds.joinToString { tweetId -> "{ \"op\": \"add\", \"tweet_id\": \"$tweetId\"}" })
    json = json.plus("]}")
    logger.info("json curation json is $json")
    val response = getTwitter()?.postResponse("https://api.twitter.com/1.1/collections/entries/curate.json", JSONObject(json))
    logger.info("response from curate for collection $collectionId is $response")
  }


}
