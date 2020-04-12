REM updates the fat jar containing the lambdas on the timstwitterlisteningparty bucket - this does not refresh the lambda itself - see update-lambda-functions.bat
aws s3 cp ..\tools\build\libs\tools-1.0.0-all.jar s3://timstwitterlisteningparty.com --grants read=uri=http://acs.amazonaws.com/groups/global/AllUsers
pause
