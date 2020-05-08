package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.timstwitterlisteningparty.tools.lambda.util.S3HtmlGenerator


/**
 * Generates the various html files and writes them back to the same s3 bucket
 * based on a scheduled event
 */
@Suppress("unused")
class ScheduledRegenerateHtmlHandler : RequestHandler<ScheduledEvent, String> {
  override fun handleRequest(input: ScheduledEvent?, context: Context?): String {
    return S3HtmlGenerator().generate()
  }
}
