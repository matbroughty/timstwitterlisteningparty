#!/bin/bash
FILES="../data/dynamodb/*"
for FILE in $FILES
do
  echo -e "Uploading $FILE to Time-Slot Table"
  aws dynamodb batch-write-item --request-items file://"$FILE"
done
pwd