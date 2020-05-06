package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvToBeanBuilder
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.*
import java.util.*


/**
 * Reads the data/time-slot-data.csv and with the resultant list of [TimeSlot]
 * writes the pages out - one for each row with a twitter list
 */
@Component
class CollectionsHtmlPagesCreator {

  private val logger = LoggerFactory.getLogger(javaClass)

  fun createTwitterListPages(fileName: String = "data/time-slot-data.csv", inputStream: InputStream? = null,
                             writeToFile: Boolean = false, newFileName: String = fileName) :  Map<String, String>{


    val csvToBeanBuilder: CsvToBeanBuilder<TimeSlot> =
      if (inputStream != null) CsvToBeanBuilder<TimeSlot>(InputStreamReader(inputStream)) else {
        CsvToBeanBuilder<TimeSlot>(FileReader(fileName))
      }

    val existingList: List<TimeSlot> = csvToBeanBuilder
      .withType(TimeSlot::class.java)
      .withIgnoreEmptyLine(true)
      .build()
      .parse()


    var template = getFreeMarker()
    existingList.filter { it.replayLink.isNotEmpty() }.forEach{it->
        val input: Map<String, TimeSlot> = mapOf(Pair("slot", it))
        if (writeToFile) {
          val pageFileName = "pages/list/collection_${it.replayId()}.html"
          val fileWriter: Writer = FileWriter(File(pageFileName))
          template.process(input, fileWriter)
        }
    }

    // TODO return a map of pairs so we can use in lambda and write each one to s3
    return mapOf(
      Pair("pages/list/${""}", "generated"))

  }


  fun getFreeMarker() : Template{

    // 1. Configure FreeMarker
    //
    // You should do this ONLY ONCE, when your application starts but we want to
    // run this as a lambda and can't use spring easily there
    val cfg = Configuration(Configuration.VERSION_2_3_0)

    // Where do we load the templates from:
    cfg.setClassForTemplateLoading(this::class.java, "/templates/")

    // Some other recommended settings:
    cfg.incompatibleImprovements = Version(2, 3, 20)
    cfg.defaultEncoding = "UTF-8"
    cfg.locale = Locale.UK
    cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER

    return cfg.getTemplate("collectionfeed.ftl")

  }


}
