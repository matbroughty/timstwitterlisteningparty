{
  "Time-Slot": [
<#list slots as slot>
  {
    "PutRequest": {
      "Item": {
        "ttlp_id": {
          "S": "${slot.listeningPartyNumber}"
        },
        "date": {
          "S": "${slot.isoDate}"
        },
        "twitter_announcement": {
          "S": "${slot.link}"
        },
        "artist": {
          "S": "${slot.band}"
        },
        "artwork_large": {
          "S": "${slot.spotifyImgLinkLarge}"
        },
        "replay_feed": {
          "S": "${slot.replayLink}"
        },
        "artwork_medium": {
          "S": "${slot.spotifyImgLink}"
        },
        "artwork_small": {
          "S": "${slot.spotifyImgLinkSmall}"
        },
        "album": {
          "S": "${slot.album}"
        },
        "twitter_collection": {
          "S": "${slot.twitterCollectionLink}"
        },
        "release_date": {
          "S": "${slot.spotifyYear}"
        },
        "album_link": {
          "S": "${slot.spotifyLink}"
        },
        "twitter_handles": {
          "S": "${slot.tweeters}"
        }
      }
    }
  }<#if slot?is_last><#else>,</#if>
</#list>
  ]
}
