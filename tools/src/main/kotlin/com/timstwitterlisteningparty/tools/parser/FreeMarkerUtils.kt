package com.timstwitterlisteningparty.tools.parser

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import java.util.*

const val COLLECTIONS_FTL = "collectionfeed.ftl"
const val TBC_FTL = "tbc.ftl"
const val ARCHIVE_FTL = "archive.ftl"
const val UPCOMING_FTL = "upcoming.ftl"
const val ALL_FTL = "all.ftl"
const val RECORD_SHOPS_FTL = "recordshops.ftl"
const val BOOK_SHOPS_FTL = "bookshops.ftl"
const val BOOK_REVIEWS_FTL = "bookreviews.ftl"
const val WALL_FTL = "wall.ftl"
const val ANNIVERSARY_FTL = "anniversary.ftl"


/**
 * Any methods handy for using the templates*.ftl files
 */
class FreeMarkerUtils {



  fun getFreeMarker(templateName: String): Template {

    val cfg = Configuration(Configuration.VERSION_2_3_0)

    // Where do we load the templates from:
    cfg.setClassForTemplateLoading(this::class.java, "/templates/")

    // Some other recommended settings:
    cfg.incompatibleImprovements = Version(2, 3, 20)
    cfg.defaultEncoding = "UTF-8"
    cfg.locale = Locale.UK
    cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER

    return cfg.getTemplate(templateName)

  }

}
