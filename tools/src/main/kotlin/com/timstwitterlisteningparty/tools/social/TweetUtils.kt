package com.timstwitterlisteningparty.tools.social

import com.timstwitterlisteningparty.tools.parser.Replay
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import com.timstwitterlisteningparty.tools.parser.TimeSlotReader
import io.github.redouane59.twitter.TwitterClient
import io.github.redouane59.twitter.dto.collections.TimeLineOrder
import io.github.redouane59.twitter.signature.TwitterCredentials
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.MonthDay
import java.time.format.DateTimeFormatter


@Component
class TweetUtils {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun getTwittered(): TwitterClient {
    logger.info("getting-twitter-client")
    return TwitterClient(
      TwitterCredentials.builder()
      .accessToken(System.getenv("twitter4j_oauth_accessToken"))
      .accessTokenSecret(System.getenv("twitter4j_oauth_accessTokenSecret"))
      .apiKey(System.getenv("twitter4j_oauth_consumerKey"))
      .apiSecretKey(System.getenv("twitter4j_oauth_consumerSecret"))
      .build())
  }


  /**
   * Tweet a message
   */
  fun tweet(msg: String): String {
    return try {
      logger.info("tweeting $msg")
      getTwittered().postTweet(msg).id
    } catch (e: Exception) {
      logger.error("Some badness with sending '$msg'  as a tweet - ${e.localizedMessage}", e)
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
      .asSequence()
      .filter { it.spotifyYear.length == 10 } // needs a full date format with month and days - i.e. 2019-05-28
      .filter { !it.spotifyThisYear() } // not an anniversary until a year has passed
      .filter { it.tweeters.isNotEmpty() } // not a silent party
      .filter { !it.is1970() } // not scheduled
      .filter {
        val releaseDate = LocalDate.parse(it.spotifyYear, DateTimeFormatter.ISO_DATE)
        now == MonthDay.of(releaseDate.month, releaseDate.dayOfMonth)
      }
      .toList()
      .forEach {
        logger.debug("found an anniversary for $it")
        anniversaryToTweet = true
        val releaseDate = LocalDate.parse(it.spotifyYear, DateTimeFormatter.ISO_DATE)
        val msg = "${releaseDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy"))}. " +
          if (it.replayLink.isEmpty()) {
            "${it.band} released ${it.album}. ${it.tweeterList().first()} will be hosting an upcoming listening party. ${it.link} #TimsTwitterListeningParty"
          } else {
            "${it.band} released ${it.album}. You can replay the listening party here ${it.replayLink} Tweets from ${it.buildTweeters(120)} #TimsTwitterListeningParty #ttlp${it.listeningPartyNumber}"
 //           "${it.band} released ${it.album}. You can replay the ${it.tweeterList().first()} listening party here ${it.replayLink} #TimsTwitterListeningParty #ttlp${it.listeningPartyNumber}"
          }
        if (logOnly) {
          logger.info(msg)
        } else {
          logger.info("tweet-anniversary $msg")
          tweet(msg)
        }
      }
    return anniversaryToTweet
  }

  fun tweetYearlyAnniversary(timeSlots: List<TimeSlot> = emptyList(), logOnly: Boolean = false): Boolean {
    val now = MonthDay.from(LocalDate.now())
    var yearlyAnniversaryToTweet = false
    val timeSlotList = if (timeSlots.isEmpty()) TimeSlotReader().timeSlots else timeSlots
    timeSlotList
      .asSequence()
      .filter { it.isoDate.year != LocalDate.now().year } // not this year
      .filter { it.tweeters.isNotEmpty() } // not a silent party
      .filter { !it.is1970() } // not scheduled
      .filter { it.hasReplay() } // needs a replay as we include that on tweet
      .filter {
        now == MonthDay.of(it.isoDate.month, it.isoDate.dayOfMonth)
      }
      .toList()
      .forEach {
        yearlyAnniversaryToTweet = true
        logger.debug("found a yearly anniversary for $it")
        val yearsAgo = LocalDate.now().year - it.isoDate.year
        val years = if (yearsAgo == 1) "year" else "years"
        val msg = "$yearsAgo $years ago today we had a listening party for ${it.album} by ${it.band}. You can find the replay here ${it.replayLink} Tweets from ${it.buildTweeters(130)} #TimsTwitterListeningParty #ttlp${it.listeningPartyNumber}"
        if (logOnly) {
          logger.info(msg)
        } else {
          logger.info("tweet--yearly-anniversary $msg")
          tweet(msg)
        }
      }
    return yearlyAnniversaryToTweet
  }

  fun tweetReplay(timeSlot: TimeSlot, replayLink: String, logOnly: Boolean = false): String {
    if (timeSlot.tweeterList().isEmpty()) {
      return "no band/artist to tweet replay"
    }
    if (!pageExists(replayLink)) {
      return "$replayLink page doesn't exist yet"
    }
    logger.info("tweeting-replay-msg for replay $replayLink")

    val msg = "${timeSlot.band} : ${timeSlot.album} replay available here $replayLink Tweets from ${timeSlot.buildTweeters(130)} #TimsTwitterListeningParty #ttlp${timeSlot.listeningPartyNumber}"

    if (logOnly) {
      logger.info(msg)
    } else {
      logger.info("tweet-replay $msg")
      tweet(msg)
    }

    return msg
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
        logger.info("added tweetid $tweetId  to first tweet collection")
        replayId++
        replayFeedHtml = try {
          Jsoup.connect("https://timstwitterlisteningparty.com/snippets/replay/feed_${replayId}_snippet.html").get()
        } catch (e: Exception) {
          // will indicate end of loop if no feed existed for feed number
          return ""
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

  fun createCollection(name: String, description: String, order: String = "tweet_chron"): String {

    val response = getTwittered().collectionsCreate(name, description, "", TimeLineOrder.CHRONOLOGICAL)
    logger.info("response from collection create $name is ${response.response.timeLineId}")
    if (response != null && !response.hasErrors()) {
      return response.response.timeLineId
    }
    logger.warn("issue creating collection for $name and $description - collectionid not known")
    return ""
  }

  /**
   * The tweetIds to add to the collectionId (not collectionid needs to be in format "custom-$collectionId")
   */
  fun addToCollection(tweetIds: List<String>, collectionId: String) {
    getTwittered().collectionsCurate(collectionId, tweetIds)
  }


}
