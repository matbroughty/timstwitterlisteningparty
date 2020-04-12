REM moves the time-slot-data.csv to the s3 bucket.  This will effectively regenerate the site as it will trigger an s3 event that will call the RegenerateHtmlHandler
aws s3 cp ..\time-slot-data.csv s3://timstwitterlisteningparty.com --grants read=uri=http://acs.amazonaws.com/groups/global/AllUsers
pause
