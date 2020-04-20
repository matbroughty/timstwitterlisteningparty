package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.event.S3EventNotification
import java.lang.System.err
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Checks to see if the s3 event was for the a .csv file (sanity check) and if it was
 * then generates the various html files and writes them back to the same s3 bucket
 */
@Suppress("unused")
class RegenerateHtmlHandler : RequestHandler<S3Event, String> {

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

      // ok, lets generate
      return S3HtmlGenerator().generate()

    } catch (e: Exception) {
      err.println("Issue with parse " + e.localizedMessage)
      throw RuntimeException(e)
    }

  }

}
