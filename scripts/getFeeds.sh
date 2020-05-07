#!/bin/bash
function usage() {
    echo Usage: getFeeds.sh [-i] [-s] [-t] [-p] [-a] id1 id2
    echo "-i : generate index"
    echo "-s : generate feed snippets (full sets of tweets)"
    echo "-t : generate feed templates (headers and twitter cards)"
    echo "-p : download album artwork"
    echo "-a : generate and download all of the above"
    echo "-g : perform github steps (up to but not including commit)"
    echo "id1, id2 : start and end party ids to generate files for"
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

function addtogit() {
    if [ $github -eq 1 ]
    then
        echo Adding $1 to git
	git add $1
    fi
}

REMOTE_PATH="http://www.sk7software.co.uk/listeningparty/replay/live/scripts"
LOCAL_PATH=".."

# Determine number of args passed in
numArgs=$#

# Must supply at least one switch and id1 and id2
if [ "$numArgs" -lt 3 ]
then
    usage
fi

# Initialise switches (all off)
index=0
pictures=0
snippet=0
template=0
github=0

# Array for holding ids
declare -a ids
declare -i idx
idx=0

# Regex for checking numbers
regex='^[-0-9]+$'

# Read in args
for i in "$@"
do
    case "$i" in
        "-i" )
            index=1
            ;;
        "-p" )
            pictures=1
            ;;
        "-s" )
            snippet=1
            ;;
        "-t" )
            template=1
            ;;
        "-g" )
            github=1
            ;;
        "-a" )
            index=1
            snippet=1
            template=1
            pictures=1
            ;;
        * )
            # Confirm not an invalid - arg
            firstchar="$(echo $i | head -c 1)"
            if [ $firstchar == "-" ]
            then
                usage
            fi
            
            # Confirm numeric
            if ! [[ $i =~ $regex ]]
            then
               usage
            fi

            # Confirm no more than 2 ids
            if [ $idx -gt 2 ]
            then
                usage
            fi

            ids[$idx]=$i
            idx=$idx+1
            ;;
    esac
done


echo "Fetch Index: $(yesno $index)"
echo "Fetch Pictures: $(yesno $pictures)"
echo "Fetch Snippets: $(yesno $snippet)"
echo "Fetch Templates: $(yesno $template)"
echo "Update github repo: $(yesno $github)"
echo "Generating feeds between party ids ${ids[0]} and ${ids[1]}"
echo
read -p "Continue (y/n)? " answer
case ${answer:0:1} in
    y|Y )
        # Do nothing
    ;;
    * )
        exit
    ;;
esac

if [ $github -eq 1 ]
then
	git pull
fi

if [ $index -eq 1 ]
then
    echo ++++++++++ Generating Index Snippet
    localfile=${LOCAL_PATH}/snippets/replay/feed_list${ids[0]}_snippet.html
    curl "${REMOTE_PATH}/indexsnip.php?levels=1" -o $localfile
    sed "s/\.\.\/snippets\/replay\/feed_.*_snippet.html/\.\.\/snippets\/replay\/feed_list${ids[0]}_snippet.html/" ${LOCAL_PATH}/pages/replay.html >tmp.txt
    mv tmp.txt ${LOCAL_PATH}/pages/replay.html
    addtogit $localfile
    addtogit ${LOCAL_PATH}/pages/replay.html
fi

for i in $(seq ${ids[@]})
do
    if [ $template -eq 1 ]
    then
        echo ++++++++++ Fetching template for feed $i
        localfile=${LOCAL_PATH}/pages/replay/feed_${i}.html
        curl "${REMOTE_PATH}/feedTemplate.php?id=${i}&levels=2" -o $localfile
        addtogit $localfile
    fi

    if [ $snippet -eq 1 ]
    then
        echo ++++++++++ Fetching snippet for feed $i
        localfile=${LOCAL_PATH}/snippets/replay/feed_${i}_snippet.html
        curl "${REMOTE_PATH}/feedsnip.php?id=${i}&levels=2" -o $localfile
        addtogit $localfile
    fi

    if [ $pictures -eq 1 ]
    then
        echo ++++++++++ Fetching artwork PNGs for feed $i
        image=${LOCAL_PATH}/pages/replay/img/feed_${i}.png
        thumbnail=${LOCAL_PATH}/pages/replay/img/feed_${i}_small.png
        curl "${REMOTE_PATH}/img/feed_${i}.png" > $image
        curl "${REMOTE_PATH}/img/feed_${i}_small.png" > $thumbnail
        addtogit $image
        addtogit $thumbnail
    fi
done

if [ $github -eq 1 ]
then
    git status
fi
