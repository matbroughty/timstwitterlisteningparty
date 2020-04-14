REM requests ScheduledHtmlGenerator with empty event - add --invocation-type Event for async
aws lambda invoke --function-name ScheduledHtmlGenerator --payload '{}' response.json
pause

