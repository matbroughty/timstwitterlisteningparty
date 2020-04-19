package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.timstwitterlisteningparty.tools.parser.*
import java.io.InputStream
import java.time.LocalDateTime

class S3HtmlGenerator {

  fun generate(bucketName: String = "timstwitterlisteningparty.com",
               srcKeyTimeSlots: String = "time-slot-data.csv",
               srcKeyRecordStores: String = "record-store-data.csv",
               srcKeyBookStores: String = "book-shops-data.csv",
               srcKeyBookReviews: String = "book-review-data.csv"
  ): String {
    print("bucket = $bucketName and file = $srcKeyTimeSlots")
    val s3Client = AmazonS3ClientBuilder.defaultClient() as AmazonS3Client
    println("Getting $srcKeyTimeSlots from bucket $bucketName")
    val s3Object = s3Client.getObject(GetObjectRequest(bucketName, srcKeyTimeSlots))
    println("Object for $srcKeyTimeSlots from bucket $bucketName is $s3Object")
    val objectData: InputStream = s3Object.objectContent

    // the time-slot data is responsible for creating a number of html snippets
    val files = TimeSlotFileCreator().createFiles(fileName = srcKeyTimeSlots, inputStream = objectData, writeToFile = false)
    files.keys.forEach {
      println("Writing to: $bucketName/${it} with html ${files[it]}")
      try {
        s3Client.putObject(bucketName, it, files[it])
      } catch (e: AmazonServiceException) {
        System.err.println("We have an error writing to  $bucketName/${it} with html ${files[it]} error is:  ${e.errorMessage}")
      }
    }

    val timeSlotsMsg = "S3 S3HtmlGenerator TimeSlots - Successfully updated $bucketName using $srcKeyTimeSlots " +
      "and uploaded to $bucketName with  ${files.map { it.key }}"
    println(timeSlotsMsg)

    val recordSlotsMsg = createHtmlFile(RecordStoreFileCreator(), bucketName, s3Client, srcKeyRecordStores, "snippets/record-stores.html")
    val bookSlotsMsg = createHtmlFile(BookStoreFileCreator(), bucketName, s3Client, srcKeyBookStores, "snippets/book-stores.html")
    val bookReviewMsg = createHtmlFile(BookReviewFileCreator(), bucketName, s3Client, srcKeyBookReviews, "snippets/book-reviews-shops.html")

    //finally write an invalidation.txt file to indicate the cloud front edge needs refreshing
    try {
      s3Client.putObject(bucketName, "aws/invalidation.txt", "semaphore file to indicate cache needs updating. a trigger will pick up this has been updated.")
    } catch (e: AmazonServiceException) {
      System.err.println("We have an error writing to  $bucketName/aws/invalidation.txt (cache refresh may not work) error is:  ${e.errorMessage}")
    }
    return "Ok: timeSlots **** + $timeSlotsMsg recordStores **** $recordSlotsMsg bookStores **** " +
      "$bookSlotsMsg bookReviews **** $bookReviewMsg : Generated time is :${LocalDateTime.now()} "
  }


  private fun createHtmlFile(fileCreator: HtmlFileCreator, bucketName: String,
                             s3Client: AmazonS3Client, dataFile: String, htmlSnippetName: String): String {
    println("Getting $dataFile from bucket $bucketName")
    val s3Object = s3Client.getObject(GetObjectRequest(bucketName, dataFile))
    println("Object for $dataFile from bucket $bucketName is $s3Object")
    val objectData = s3Object.objectContent
    //
    val htmlSnippetStr = fileCreator.createFiles(fileName = dataFile, inputStream = objectData, writeToFile = false) as String
    println("Writing to: $bucketName with html $htmlSnippetStr")
    var msg = "S3 S3HtmlGenerator"
    try {
      s3Client.putObject(bucketName, htmlSnippetName, htmlSnippetStr)
    } catch (e: AmazonServiceException) {
      System.err.println("We have an error writing $dataFile to $bucketName with html $htmlSnippetStr error is:  ${e.errorMessage}")
      return msg.plus(" FAILED to update $bucketName using $dataFile and uploaded to $bucketName with object $htmlSnippetName}")
    }
     msg = msg.plus("- Successfully updated $bucketName using $dataFile and uploaded to $bucketName with object $htmlSnippetName}")
    println(msg)
    return msg
  }

}
