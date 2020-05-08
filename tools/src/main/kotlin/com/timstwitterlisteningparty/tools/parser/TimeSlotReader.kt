package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import java.io.FileReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Reads in the time-slot-data.csv file - by default from data/time-slot-data.csv.  If the
 * inputStream is not null then will use that (main used when called with data from S3 bucket)
 */
data class TimeSlotReader(val timeSlotFile: String = "data/time-slot-data.csv", val inputStream: InputStream? = null) {

  var timeSlots: List<TimeSlot>

  init {
    val csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> =
      if (inputStream != null) CsvToBeanBuilder<TimeSlot>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<TimeSlot>(FileReader(timeSlotFile))
      }
    timeSlots = csvToBeanBuilder.withType(TimeSlot::class.java).withIgnoreEmptyLine(true).build().parse()
  }

}
