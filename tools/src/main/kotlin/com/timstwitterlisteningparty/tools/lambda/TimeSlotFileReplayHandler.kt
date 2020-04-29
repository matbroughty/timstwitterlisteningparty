package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.timstwitterlisteningparty.tools.parser.TimeSlotFileReplayLink
import com.timstwitterlisteningparty.tools.twitter.TweetUtils
import java.io.InputStream

/**
 * Add a column (5th) to the data/time-slot.date.csv file from the url view-source: https://timstwitterlisteningparty.com/snippets/replay/feed_index_snippet.html")
 */
@Suppress("unused")
class TimeSlotFileReplayHandler(private val bucketName: String = "timstwitterlisteningparty.com",
                                private val srcKeyTimeSlots: String = "data/time-slot-data.csv") : RequestHandler<S3Event, String> {

  override fun handleRequest(input: S3Event?, context: Context?): String {
    print("bucket = $bucketName and file = $srcKeyTimeSlots")
    val s3Client = AmazonS3ClientBuilder.defaultClient() as AmazonS3Client
    println("Getting $srcKeyTimeSlots from bucket $bucketName")
    val s3Object = s3Client.getObject(GetObjectRequest(bucketName, srcKeyTimeSlots))
    println("Object for $srcKeyTimeSlots from bucket $bucketName is $s3Object")
    val objectData: InputStream = s3Object.objectContent
    // no spring injection in the lambda
    val fileData = TimeSlotFileReplayLink(TweetUtils()).addReplayLink(inputStream = objectData)
    print("fileData = $fileData")
    //sanity check
    var msg = "TimeSlotFileReplayHandler"
    if(fileData.isNotEmpty() && fileData.length > 100){
      println("Writing to: $bucketName file $srcKeyTimeSlots ")
      try {
        s3Client.putObject(bucketName, srcKeyTimeSlots, fileData)
      } catch (e: AmazonServiceException) {
        System.err.println("We have an error writing to  $bucketName and file $srcKeyTimeSlots error is:  ${e.errorMessage}")

        msg = msg.plus(" failed")

      }
    }

    msg = msg.plus(" was a success!!!!!")

    println(msg)
    return fileData
  }
}
