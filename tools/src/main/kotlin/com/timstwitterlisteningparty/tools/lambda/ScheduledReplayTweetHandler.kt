package com.timstwitterlisteningparty.tools.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.timstwitterlisteningparty.tools.lambda.util.S3ReplayTweetGenerator


/**
 * Generates the replay tweets if required.
 * [S3ReplayTweetGenerator.tweetReplay]
 */
@Suppress("unused")
class ScheduledReplayTweetHandler : RequestHandler<ScheduledEvent, String>{
  override fun handleRequest(input: ScheduledEvent?, context: Context?): String {
    return S3ReplayTweetGenerator().tweetReplay()
  }
}
