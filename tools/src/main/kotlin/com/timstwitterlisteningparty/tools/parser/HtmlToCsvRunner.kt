package com.timstwitterlisteningparty.tools.parser

import com.opencsv.CSVWriter
import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileWriter
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.time.temporal.TemporalField
import java.util.*
import java.util.stream.Collectors


/**
 * Takes the existing HTML file(s) (just index.html really) and creates
 * a csv file from it with the date strings converted to Iso date/time
 */
@Component
class HtmlToCsvRunner() : CommandLineRunner {
  private val logger = LoggerFactory.getLogger(javaClass)
  override fun run(vararg args: String?) {
    logger.info("args passed in {} ", args)

    var fileName = "index.html"
    if (args.isEmpty()) {
      logger.warn("No arguments passed defaulting to {}", fileName)
    }else{
      fileName = args[0]?: ""
    }

    logger.info("Parsing File from '{}'", fileName)
    val input = File(fileName)
    val doc: Document = Jsoup.parse(input, "UTF-8")

    val csvRows = doc.select("tr")
      .stream()
      .filter { it.children().`is`("th").not() }
      .map { it.buildCsvRow() }
      .filter { it != null }
      .collect(Collectors.toList())

    csvRows.forEach { logger.info("TimeSlot is: {}", it) }
    val fileWriter = FileWriter("time-slot-data.csv")
    val sbc = StatefulBeanToCsvBuilder<TimeSlot>(fileWriter)
      .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
      .build()
    sbc.write(csvRows)
    fileWriter.close()
  }

  private fun Element?.buildCsvRow(): TimeSlot? {
    if (this != null) {
      logger.debug("row {}", html())
      return TimeSlot(
        select("td")[0].text(),
        select("td")[1].text(),
        select("td")[2].text(),
        select("td")[3].text(),
        select("td")[4].select("a").attr("href")
      )
    }
    return null;
  }

  data class TimeSlot(val dateStr: String,
                      val timeStr: String,
                      @CsvBindByPosition(position = 1) val band: String,
                      @CsvBindByPosition(position = 2) val album: String,
                      @CsvBindByPosition(position = 3) val link: String){

    private val logger = LoggerFactory.getLogger(javaClass)

    @CsvBindByPosition(position = 0)
    private val _date: LocalDateTime? = buildDate(dateStr, timeStr)
    val date: LocalDateTime
      get() = this._date!!

    private fun buildDate(dateStr: String, timeStr: String) : LocalDateTime?{
      logger.info("Parsing date {} and time {} for band {} and album", dateStr, timeStr, band, album)
      // no date yet
      if(dateStr.contains("?")){
        return null
      }

      val parseFormatter = DateTimeFormatterBuilder()
        .appendPattern("EEEE d['st']['nd']['rd']['th'] MMMM")
        .parseDefaulting(ChronoField.YEAR, 2020)
        .toFormatter(Locale.ENGLISH)
      val date = LocalDate.parse(dateStr, parseFormatter).withYear(2020)
      logger.info("Parsed date = {} ", date)
      val time = Integer.parseInt(StringUtils.substringBefore(timeStr, ":"))
      logger.info("Parsed time = {} ", time)
      return LocalDateTime.of(date, LocalTime.of(time + 12, 0))
    }

  }
}
