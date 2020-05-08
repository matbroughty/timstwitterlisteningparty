package com.timstwitterlisteningparty.tools.parser

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import freemarker.template.Version
import java.util.*

const val COLLECTIONS_FTL = "collectionfeed.ftl"
const val TBC_FTL = "tbc.ftl"
const val RECORD_SHOPS_FTL = "recordshops.ftl"
const val BOOK_SHOPS_FTL = "bookshops.ftl"
const val BOOK_REVIEWS_FTL = "bookreviews.ftl"

/**
 * Any methods handy for using the templates*.ftl files
 */
class FreeMarkerUtils {





  fun getFreeMarker(templateName: String): Template {

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

    return cfg.getTemplate(templateName)

  }

}
