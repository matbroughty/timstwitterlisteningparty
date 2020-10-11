#!/bin/bash
RED='\033[0;31m'
NC='\033[0m'
ERROR=0

function usage() {
    echo Usage: getFeeds.sh [-i] [-s] [-t] [-p] [-a] id1 id2
    echo "-i : generate index"
    echo "-s : generate feed snippets (full sets of tweets)"
    echo "-t : generate feed templates (headers and twitter cards)"
    echo "-p : download album artwork"
    echo "-u : update all pending tour dates"
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

function verify() {
    file=$1
    line=$2
    result=`tail -2 "$file" | grep "$line" | wc -l`
    if [ $result -eq 0 ]
    then
        echo -e "${RED}********************** FILE $file ERROR *********************${NC}"
        ERROR=1
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
tours=0
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
        "-u" )
            tours=1
            ;;
        "-g" )
            github=1
            ;;
        "-a" )
            index=1
            snippet=1
            template=1
            pictures=1
            tours=1
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
echo "Fetch Updated tours: $(yesno $tours)"
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
    localfile1=${LOCAL_PATH}/snippets/replay/feed_list${ids[0]}_snippet.html
    localfile2=${LOCAL_PATH}/snippets/replay/replay_home${ids[0]}_snippet.html
    localfile3=${LOCAL_PATH}/snippets/replay/replay_date${ids[0]}_snippet.html
    localfile4=${LOCAL_PATH}/snippets/replay/replay_artist${ids[0]}_snippet.html
    localfile5=${LOCAL_PATH}/snippets/mixtape-snippet-${ids[0]}.html
    localfile6=${LOCAL_PATH}/snippets/stats.html
    curl "${REMOTE_PATH}/indexsnip.php?levels=1" -o $localfile1
    verify "$localfile1" "section"
    curl "${REMOTE_PATH}/replaysnip.php?levels=1" -o $localfile2
    verify "$localfile2" "script"
    curl "${REMOTE_PATH}/statssnip.php" -o $localfile6
    verify "$localfile6" "script"
    curl "${REMOTE_PATH}/mixtape.php" -o $localfile5
    verify "$localfile5" "script"
    sed "s/\.\.\/snippets\/replay\/feed_.*_snippet.html/\.\.\/snippets\/replay\/feed_list${ids[0]}_snippet.html/" ${LOCAL_PATH}/pages/replay.html >tmp.txt
    mv tmp.txt ${LOCAL_PATH}/pages/replay.html
    sed "s/\.\.\/snippets\/replay\/replay_home.*_snippet.html/\.\.\/snippets\/replay\/replay_home${ids[0]}_snippet.html/" ${LOCAL_PATH}/pages/replay.html >tmp.txt
    mv tmp.txt ${LOCAL_PATH}/pages/replay.html
    sed "s/\.\.\/snippets\/mixtape-snippet-.*.html/\.\.\/snippets\/mixtape-snippet-${ids[0]}.html/" ${LOCAL_PATH}/pages/mixtape.html >tmp.txt
    mv tmp.txt ${LOCAL_PATH}/pages/mixtape.html
    curl "${REMOTE_PATH}/replaysort.php?levels=1&sort=date" -o $localfile3
    verify "$localfile3" "div"
    curl "${REMOTE_PATH}/replaysort.php?levels=1&sort=artist" -o $localfile4
    verify "$localfile4" "div"
    sed "s/XX_PARTY_ID_XX/${ids[0]}/" ${localfile2} >tmp.txt
    mv tmp.txt ${localfile2}

    addtogit $localfile1
    addtogit $localfile2
    addtogit $localfile3
    addtogit $localfile4
    addtogit $localfile5
    addtogit $localfile6
    addtogit ${LOCAL_PATH}/pages/replay.html
    addtogit ${LOCAL_PATH}/pages/mixtape.html

    grep "<h1 class=\"content-subhead\">" ../snippets/replay/replay_home${ids[0]}_snippet.html
fi

for i in $(seq ${ids[@]})
do
    if [ $template -eq 1 ]
    then
        echo ++++++++++ Fetching template for feed $i
        localfile=${LOCAL_PATH}/pages/replay/feed_${i}.html
        curl "${REMOTE_PATH}/feedTemplate.php?id=${i}&levels=2" -o $localfile
        verify "$localfile" "html"
        addtogit $localfile
    fi

    if [ $snippet -eq 1 ]
    then
        echo ++++++++++ Fetching snippet for feed $i
        localfile=${LOCAL_PATH}/snippets/replay/feed_${i}_snippet.html
        curl "${REMOTE_PATH}/feedsnip.php?id=${i}&levels=2&notour=1" -o $localfile
        verify "$localfile" "section"
        addtogit $localfile
    fi

    if [ $tours -eq 1 ]
    then
        ./getUpdatedTours.sh $github
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

if [ $ERROR -ne 0 ]
then
    ehco -e "${RED}There have been errors${NC}"
fi