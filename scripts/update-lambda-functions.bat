REM updates the lambdas with the latest code in the timstwitterlisteningparty.com bucket - the lambdas here all share the same tools.jar but need refreshing separately
aws lambda update-function-code --function-name html-generator --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
pause
REM now the next function ScheduledHtmlGenerator
aws lambda update-function-code --function-name ScheduledHtmlGenerator --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
pause
REM now the next function ScheduledHtmlGenerator
aws lambda update-function-code --function-name InvalidateCacheHandler --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
pause
REM now the final function
aws lambda update-function-code --function-name TimeSlotReplayHandler --s3-bucket timstwitterlisteningparty.com --s3-key tools-1.0.0-all.jar
pause
