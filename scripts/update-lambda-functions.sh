#!/bin/bash
aws lambda update-function-code --function-name html-generator --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
aws lambda update-function-code --function-name ScheduledHtmlGenerator --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
aws lambda update-function-code --function-name ScheduledReplayTweeter --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
aws lambda update-function-code --function-name TimeSlotFileEnrichHandler --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
aws lambda update-function-code --function-name ScheduledAnniversaryTweeter --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
