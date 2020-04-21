@file:Suppress("unused")

package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest
import com.amazonaws.services.cloudfront.model.InvalidationBatch
import com.amazonaws.services.cloudfront.model.Paths
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.event.S3EventNotification
import java.time.LocalDateTime


/**
 * Programmatically invalidate the cloud front cache.  This will
 * clear the whole cache
 */
class InvalidateCacheHandler : RequestHandler<S3Event, String> {

  override fun handleRequest(s3event: S3Event, context: Context?): String {
    val record: S3EventNotification.S3EventNotificationRecord = s3event.records[0]
    val srcBucket = record.s3.bucket.name
    val srcKey = record.s3.getObject().urlDecodedKey
    val lastModified = AmazonS3ClientBuilder.defaultClient().getObject(srcBucket, srcKey).objectMetadata.lastModified
//    // only do the invalidation if the file we are checking has
//    return if (ChronoUnit.MINUTES.between(lasModified, LocalDateTime.now()) > 1) {
      val invalidationBatch = InvalidationBatch(Paths().withItems("/").withQuantity(1), LocalDateTime.now().toString())
      AmazonCloudFrontClientBuilder
        .defaultClient()
        .createInvalidation(CreateInvalidationRequest("E3VMVDBEUWOSL5", invalidationBatch))
      return "Created invalidation at ${LocalDateTime.now()} for $srcBucket and $srcKey and last modified $lastModified}"
//    } else {
//      "DID NOT create invalidation at ${LocalDateTime.now()} for $srcBucket and $srcKey and last modified ${AmazonS3ClientBuilder.defaultClient().getObject(srcBucket, srcKey).objectMetadata.lastModified}"
//    }
  }
}
