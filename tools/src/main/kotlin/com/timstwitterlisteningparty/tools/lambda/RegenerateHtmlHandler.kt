package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.event.S3EventNotification
import com.amazonaws.services.s3.model.GetObjectRequest
import com.timstwitterlisteningparty.tools.parser.FileCreator
import java.io.InputStream
import java.lang.System.err
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Checks to see if the event was for the time-slot-data.csv and if it was
 * then generates the
 */
@Suppress("unused")
class RegenerateHtmlHandler : RequestHandler<S3Event,String>{

  override fun handleRequest(s3event: S3Event, context: Context?): String {
    try {
      println("In Handle Request with: $s3event")

      val record: S3EventNotification.S3EventNotificationRecord = s3event.records[0]
      val srcBucket = record.s3.bucket.name
      // Object key may have spaces or unicode non-ASCII characters.
      val srcKey = record.s3.getObject().urlDecodedKey
      println("srcKey = $srcKey from bucket $srcBucket")

      // Get file type
      val matcher: Matcher = Pattern.compile(".*\\.([^.]*)").matcher(srcKey)
      if (!matcher.matches()) {
        println("Unable to infer file type for key $srcKey")
        return ""
      }
      val csvType: String = matcher.group(1)
      if ("csv" != csvType) {
        println("Skipping non-csv file $srcKey")
        return ""
      }

      val s3Client = AmazonS3ClientBuilder.defaultClient()
      println("Getting $srcKey from bucket $srcBucket")
      val s3Object = s3Client.getObject(GetObjectRequest(srcBucket, srcKey))
      println("Object for $srcKey from bucket $srcBucket is $s3Object")
      val objectData: InputStream = s3Object.objectContent

      val files = FileCreator().createFiles(inputStream = objectData, writeToFile = false)

      files.keys.forEach{
        println("Writing to: $srcBucket/${it} with html ${files[it]}")
        try {
          s3Client.putObject(srcBucket,it, files[it])
        } catch (e: AmazonServiceException) {
          err.println("We have an error writing to  $srcBucket/${it} with html ${files[it]} error is:  ${e.errorMessage}")
        }
      }

      println("Successfully updated " + srcBucket.toString() + "/ using "
        + srcKey.toString() + " and uploaded to " + srcBucket.toString() + "/" + files.map {it.key }.toString())
      return "Ok"


    }catch(e : Exception){
      err.println("Issue with parse " + e.localizedMessage)
      throw RuntimeException(e)
    }

  }

}
