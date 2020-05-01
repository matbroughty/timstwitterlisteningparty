package com.timstwitterlisteningparty.tools.lambda.util

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.opencsv.bean.CsvToBeanBuilder
import com.timstwitterlisteningparty.tools.parser.ReplayPHPScript
import com.timstwitterlisteningparty.tools.parser.TimeSlot
import com.timstwitterlisteningparty.tools.parser.TimeSlotFileReplayLink
import com.timstwitterlisteningparty.tools.twitter.TweetUtils
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime

/**
 * Uses the [TimeSlotFileReplayLink.readPhpReplayScript] to get the current replays
 * and then checks against the time-slot-data.csv in the s3 bucket and tweets any replays
 * that are missing the s3 time-slot-data.csv but are in the
 */
class S3ReplayTweetGenerator {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun tweetReplay(bucketName: String = "timstwitterlisteningparty.com",
                  srcKeyTimeSlots: String = "data/time-slot-data.csv"
  ): String {
    print("bucket = $bucketName and file = $srcKeyTimeSlots")
    val s3Client = AmazonS3ClientBuilder.defaultClient() as AmazonS3Client
    println("Getting $srcKeyTimeSlots from bucket $bucketName")
    val s3Object = s3Client.getObject(GetObjectRequest(bucketName, srcKeyTimeSlots))
    println("Object for $srcKeyTimeSlots from bucket $bucketName is $s3Object")
    val objectData: InputStream = s3Object.objectContent
    val csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> = CsvToBeanBuilder<TimeSlot>(InputStreamReader(objectData))
    val existingList: List<TimeSlot> = csvToBeanBuilder.withType(TimeSlot::class.java).withIgnoreEmptyLine(true).build().parse()
    val replayMap = ReplayPHPScript().readPhpReplayScript()
    var tweetMsg = ""
    var missingData = ""
    existingList.forEach {
      if (replayMap.containsKey(it.hashBandAlbum())) {
        val replay = replayMap[it.hashBandAlbum()]
        if (replay != null) {
          // a new replay - tell the world
          if (it.replayLink.isEmpty() && replay.fullReplayLink().isNotEmpty()) {
            logger.info("we have a new replay for:  $replay")
            val tweet = tweetMsg.plus(TweetUtils().tweetReplay(it, replayLink = replay.fullReplayLink()))
            tweetMsg = tweetMsg.plus("\n").plus(tweet)
            logger.info("tweeted $tweet")
          } else {
            logger.info("Already tweeted about replay for:  $replay")
          }
        }
      } else {
        missingData = missingData.plus("!!!").plus(it)
        logger.info("Didn't find a match in the php for $it")

      }
    }
    return "Ok: replayLink tweeted the following replay tweets $tweetMsg !!!! Did not match $missingData !!! : Generated time is :${LocalDateTime.now()} "
  }

}
