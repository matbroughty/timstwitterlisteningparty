package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.timstwitterlisteningparty.tools.parser.FileCreator
import java.io.InputStream
import java.time.LocalDateTime

class S3HtmlGenerator {

  fun generate(bucketName: String = "timstwitterlisteningparty.com", srcKey: String = "time-slot-data.csv") : String{
    print("bucket = $bucketName and file = $srcKey")
    val s3Client = AmazonS3ClientBuilder.defaultClient()
    println("Getting $srcKey from bucket $bucketName")
    val s3Object = s3Client.getObject(GetObjectRequest(bucketName, srcKey))
    println("Object for $srcKey from bucket $bucketName is $s3Object")
    val objectData: InputStream = s3Object.objectContent
    val files = FileCreator().createFiles(inputStream = objectData, writeToFile = false)

    files.keys.forEach{
      println("Writing to: $bucketName/${it} with html ${files[it]}")
      try {
        s3Client.putObject(bucketName,it, files[it])
      } catch (e: AmazonServiceException) {
        System.err.println("We have an error writing to  $bucketName/${it} with html ${files[it]} error is:  ${e.errorMessage}")
      }
    }
    val message = "S3 S3HtmlGenerator - Successfully updated $bucketName using $srcKey and uploaded to $bucketName with  ${files.map {it.key }}"
    println(message)
    return "Ok: + $message Generated time is :${LocalDateTime.now()} "
  }


}
