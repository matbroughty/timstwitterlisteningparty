package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.timstwitterlisteningparty.tools.parser.RecordStoreFileCreator
import com.timstwitterlisteningparty.tools.parser.TimeSlotFileCreator
import java.io.InputStream
import java.time.LocalDateTime

class S3HtmlGenerator {

  fun generate(bucketName: String = "timstwitterlisteningparty.com", srcKeyTimeSlots: String = "time-slot-data.csv", srcKeyRecordStores: String = "record-store-data.csv") : String{
    print("bucket = $bucketName and file = $srcKeyTimeSlots")
    val s3Client = AmazonS3ClientBuilder.defaultClient()
    println("Getting $srcKeyTimeSlots from bucket $bucketName")
    var s3Object = s3Client.getObject(GetObjectRequest(bucketName, srcKeyTimeSlots))
    println("Object for $srcKeyTimeSlots from bucket $bucketName is $s3Object")
    var objectData: InputStream = s3Object.objectContent
    val files = TimeSlotFileCreator().createFiles(fileName = srcKeyTimeSlots, inputStream = objectData, writeToFile = false)

    files.keys.forEach{
      println("Writing to: $bucketName/${it} with html ${files[it]}")
      try {
        s3Client.putObject(bucketName,it, files[it])
      } catch (e: AmazonServiceException) {
        System.err.println("We have an error writing to  $bucketName/${it} with html ${files[it]} error is:  ${e.errorMessage}")
      }
    }

    val timeSlotsMsg = "S3 S3HtmlGenerator TimeSlots - Successfully updated $bucketName using $srcKeyTimeSlots and uploaded to $bucketName with  ${files.map {it.key }}"
    println(timeSlotsMsg)

    println("Getting $srcKeyRecordStores from bucket $bucketName")
    s3Object = s3Client.getObject(GetObjectRequest(bucketName, srcKeyRecordStores))
    println("Object for $srcKeyRecordStores from bucket $bucketName is $s3Object")
    objectData = s3Object.objectContent
    val fileStr = RecordStoreFileCreator().createFiles(fileName = srcKeyRecordStores, inputStream = objectData, writeToFile = false)
    println("Writing to: $bucketName with html $fileStr")
    try {
      s3Client.putObject(bucketName,"snippets/record-stores.html", fileStr)
    } catch (e: AmazonServiceException) {
      System.err.println("We have an error writing snippets/record-stores.html to $bucketName with html $fileStr error is:  ${e.errorMessage}")
    }

    val recordSlotsMsg = "S3 S3HtmlGenerator RecordShops - Successfully updated $bucketName using $srcKeyRecordStores and uploaded to $bucketName with object snippets/record-stores.html}"
    println(timeSlotsMsg)


    return "Ok: timeSlots **** + $timeSlotsMsg recordStores **** $recordSlotsMsg : Generated time is :${LocalDateTime.now()} "
  }


}
