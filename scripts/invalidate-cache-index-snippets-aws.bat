REM creates an invalidation on the cloud front cache to ensure the latest changes in the s3 bucket are visible
aws cloudfront create-invalidation --distribution-id E3VMVDBEUWOSL5 --paths "/index.html" "/snippets*"
pause
