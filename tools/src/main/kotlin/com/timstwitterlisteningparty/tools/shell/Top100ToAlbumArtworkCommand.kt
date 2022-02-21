package com.timstwitterlisteningparty.tools.shell

import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.timstwitterlisteningparty.tools.parser.*
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import java.io.FileWriter
import java.io.StringWriter
import kotlin.streams.toList


/**
 * Pulls top 100 artwork from s3 bucket
 * <pre>
 * https://s3.eu-west-2.amazonaws.com/timstwitterlisteningparty.com/snippets/top100_snippet.html
 * </pre>
 * And generates an album artwork page for it
 */
@ShellComponent
class Top100ToAlbumArtworkCommand {
  private val logger = LoggerFactory.getLogger(javaClass)

  @ShellMethod("Builds up the pages/top100_wall.html from the " +
    "https://s3.eu-west-2.amazonaws.com/timstwitterlisteningparty.com/snippets/top100_snippet.html")
  fun top100(): String {
    return "The generated pages/top100_wall.html file was created:${createFile()} "
  }

  fun createFile(): Boolean {
    val doc: Document = Jsoup.connect("https://s3.eu-west-2.amazonaws.com/timstwitterlisteningparty.com/snippets/top100_snippet.html").get()
    val replayLinks = doc.select("a")
      .stream()
      .map { it.attr("href").replace("../", "https://timstwitterlisteningparty.com/") }
      .toList()


    val top100 = replayLinks.stream().map { findTimeSlot(it) }.toList()

    val completedList: List<List<TimeSlot?>>  = top100.chunked(10).toList();

    val template = FreeMarkerUtils().getFreeMarker(WALL_FTL)

    val input: Map<String, Any> = mapOf(
      Pair("fullSize", true),
      Pair("top100", true),
      Pair("completed_list", completedList),
      Pair("upcoming_list", completedList))

    val htmlStr = StringWriter()
    template.process(input, htmlStr)

    val fileWriter = FileWriter("pages/top100_wall.html")
    fileWriter.write(htmlStr.toString())
    fileWriter.close()


    return replayLinks.isNotEmpty()

  }

  private fun findTimeSlot(replayLink: String): TimeSlot? {
    val beans = TimeSlotReader().timeSlots
    return beans.find { it.replayLink.trim() == replayLink.trim() }
  }


}
