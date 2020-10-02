#!/bin/bash
function usage() {
    echo Usage: getUpdatedTours.sh
    exit -1
}

function yesno() {
    if [ $1 != "0" ]
    then
        echo "YES"
    else
        echo "NO"
    fi
}

REMOTE_PATH="http://www.sk7software.co.uk/listeningparty/replay/live/scripts"
LOCAL_PATH=".."

idList=`curl "${REMOTE_PATH}/songkick/updatedIds.php"`

if [ -n "$idList" ]
then
    ids=(${idList//,/ })

    # Loop round ids and fetch the snippet
    for id in "${ids[@]}"
    do
        if [ -n "$id" ]
        then
            localfile=${LOCAL_PATH}/snippets/tours/tour_${id}_snippet.html
            curl "${REMOTE_PATH}/songkick/songkick.php?skid=${id}" -o $localfile

            # Delete empty files
            size=`wc -c ${localfile} | awk '{print $1}'`
            if [ $size -lt 10 ]
            then
                rm ${localfile}
            else
                if [ $1 -eq 1 ]
                then
                    git add ${localfile}
                fi
            fi
        fi
    done
fi