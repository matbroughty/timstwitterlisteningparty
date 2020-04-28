![header](img/ttlp-small.jpg)

[![Build Status](https://travis-ci.com/matbroughty/timstwitterlisteningparty.svg?branch=master)](https://travis-ci.com/matbroughty/timstwitterlisteningparty)

## timstwitterlisteningparty

Simple [website] page to hold the dates/times and bands and albums for [#timstiwtterlisteningparty].

[website]: http://www.timstwitterlisteningparty.com
[#timstiwtterlisteningparty]: https://twitter.com/hashtag/timstwitterlisteningparty?src=hash


## Static HTML

The static html code to serve a simple blog type template with tables of data - one for each week of listening party's.

Layout examples using [Pure CSS][pure] compiled from the [pure-site][] project.

[pure]: http://purecss.io/
[pure-site]: https://github.com/pure-css/pure-site

Chucked in some bootstrap as well for the cards - and a [new design is probably on its way using all bootstrap](http://test.timstwitterlisteningparty.com)
as the [pure] library not really updated anymore

## Data

The time slot website data is driven by a simple csv file - which can be found [here](data/time-slot-data.csv) and is in the form:

|date            |band                              |album                               |twitter_link                                                  |replay_link                                                    |tweeters                                                       |
|----------------|----------------------------------|------------------------------------|--------------------------------------------------------------|---------------------------------------------------------------|---------------------------------------------------------------|
|2020-04-06T21:00|Shame                             |Songs of Praise                     |https://twitter.com/shamebanduk/status/1244924532068057088    |https://timstwitterlisteningparty.com/pages/replay/feed_11.html|@shamebanduk:@Tim_Burgess                                      |
|2020-04-06T22:00|Super Furry Animals               |Mwng                                |https://twitter.com/gruffingtonpost/status/1242519081946877953|https://timstwitterlisteningparty.com/pages/replay/feed_12.html|@gruffingtonpost:@petefowlerart:@superfurry:@Tim_Burgess       |
|2020-04-07T21:00|Field Music                       |Making A New World                  |https://twitter.com/fieldmusicmusic/status/1247448489862275073|https://timstwitterlisteningparty.com/pages/replay/feed_13.html|@AndrewJWL:@fieldmusicmusic:@Tim_Burgess                       |
|2020-04-07T22:00|The Cribs                         |Men's Needs, Women's Needs, Whatever|https://twitter.com/thecribs/status/1247422279853559817       |https://timstwitterlisteningparty.com/pages/replay/feed_14.html|@alkapranos:@RossJarman:@ryanjamesjarman:@thecribs:@Tim_Burgess|
|2020-04-08T21:00|The Lovely Eggs                   |I am Moron                          |https://twitter.com/Tim_Burgess/status/1247643296740052992    |https://timstwitterlisteningparty.com/pages/replay/feed_16.html|@TheLovelyEggs:@Tim_Burgess                                    |
|2020-04-08T22:00|The Flaming Lips                  |The Soft Bulletin                   |https://twitter.com/Tim_Burgess/status/1244305878431522818    |https://timstwitterlisteningparty.com/pages/replay/feed_17.html|@stevendrozd:@theflaminglips:@Tim_Burgess:@waynecoyne          |
|2020-04-09T20:00|Sleaford Mods                     |Austerity Dogs                      |https://twitter.com/sleafordmods/status/1245357314619686912   |https://timstwitterlisteningparty.com/pages/replay/feed_20.html|@sleafordmods:@Tim_Burgess                                     |
|2020-04-09T21:00|Pigs Pigs Pigs Pigs Pigs Pigs Pigs|Viscerals                           |https://twitter.com/Pigsx7/status/1244988772019773444         |https://timstwitterlisteningparty.com/pages/replay/feed_21.html|@Pigsx7:@Tim_Burgess                                           |
|2020-04-09T22:00|Orange Juice                      |You Cant Hide Your Love Forever     |https://twitter.com/Tim_Burgess/status/1242598621893312515    |https://timstwitterlisteningparty.com/pages/replay/feed_22.html|@EdwynCollins:@GraceMaxwell:@Steveplustax:@Tim_Burgess         |
|2020-04-10T20:00|British Sea Power                 |Open Season                         |https://twitter.com/BSPOfficial/status/1246855563184607232    |https://timstwitterlisteningparty.com/pages/replay/feed_23.html|@BSPOfficial:@Tim_Burgess                                      |
|2020-04-10T21:00|Jane Weaver                       |Modern Kosmology                    |https://twitter.com/JanelWeaver/status/1245800566732853248    |https://timstwitterlisteningparty.com/pages/replay/feed_24.html|@JanelWeaver:@Tim_Burgess                                      |
|2020-04-10T22:00|Mogwai                            |Come On Die Young                   |https://twitter.com/mogwaiband/status/1244223427239653381     |https://timstwitterlisteningparty.com/pages/replay/feed_25.html|@mogwaiband:@Tim_Burgess                                       |


The record shop website data controlled another simple csv file - which can be found [here](data/record-store-data.csv) and is in the form:

|NAME            |ADDRESS                                                         |WEBSITE                                              |TWITTER                             |
|----------------|----------------------------------------------------------------|-----------------------------------------------------|------------------------------------|
|Creekside Vinyl |3 Market Street,Faversham, Kent ME13 7AH                        |https://creeksidevinyl.co.uk/                        |https://twitter.com/Creeksidevinyl  |
|Cliffs Margate  |172 Northdown Road Margate, England, CT9 2QN United Kingdom     |http://www.cliffsmargate.com                         |https://twitter.com/cliffsmargate   |
|Ben Oneill      |64 O'Connell Street, Dungarvan, Co. Waterford, Ireland, X35 HF54|http://www.benoneilldungarvan.com/                   | https://twitter.com/BenONeillRecord|
|Freebird Records|15a Wicklow Street Dublin                                       | https://freebirdrecords.com/                        |https://twitter.com/FreebirdRecords |
|Bella Union     |Online BN1 1AJ Brighton                                         |https://bellaunion.ochre.store/bella-union-vinyl-shop|https://twitter.com/VinylBella      |
|Spiller Records |27 The Morgan Arcade, CARDIFF CF10 1AF                          | http://spillersrecords.com/                         |https://twitter.com/spillersrecords |

This is also used to drive the [google map](https://drive.google.com/open?id=1XhFWnejDpNMuz2qG6iIOt5WIAdcEXFjX&usp=sharing)

The bookshop data is driven by two files - the [reviews](data/book-review-data.csv)

|Author                           |Title                                                                        |Description                                                                                                                                                                                                                                                                                                                                                                                                       |Reviewer Twitter                    |Initials|Website                                                                                       |
|---------------------------------|-----------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|--------|----------------------------------------------------------------------------------------------|
|Bob Stanley                      |Yeah Yeah Yeah: The Story of Modern Pop                                      |As well as being in one of my favourite bands, St. Etienne, Bob Stanley has been a top music journalist for years. In this book he recounts the history of pop music from 1952 onwards; the detail is encyclopaedic, and the stories are eye-opening.                                                                                                                                                             |https://twitter.com/Tim_Burgess     |(TB)    |  https://blackwells.co.uk/bookshop/product/Yeah-Yeah-Yeah-by-Bob-Stanley-author/9780571322404|
|Dave Haslam                      |Searching for Love: Courtney Love in Liverpool, 1982                         |This short book – perfect for these discombobulating times – debunks the myths around the five months Love spent in Liverpool in 1982, apparently hanging out with Julian Cope and the Bunnymen. Love is a great story teller, but is she also an unreliable narrator?                                                                                                                                            |https://twitter.com/amy_raphael     |(AR)    | https://www.confingopublishing.uk/product-page/easter-offer-3-for-20-22-50-outside-uk        |
|Pete Paphides                    |Broken Greek                                                                 |Pete’s memories of growing up in Birmingham in an immigrant, Greek household, and finding friendship with music is so engaging. He’s barely into his teens by the end of the book. We need more.                                                                                                                                                                                                                  | https://twitter.com/Tim_Burgess    |(TB)    | https://www.foxlanebooks.co.uk/                                                              |
|Tim Burgess                      |One, Two, Another                                                            |After Tim’s revealing and so readable autobiography ‘Telling Stories’ he took us round the world in his ‘Tim Book Two’ about his passion for records, and record shops from Istanbul to San Francisco. ‘One, Two Another’ is the Tim Burgess lyrics collection, interspersed with his stories and memories from writing songs over the last thirty years; a great way to tell a story of a life immersed in music.|https://twitter.com/Mr_Dave_Haslam  | (DH)   |  http://www.claphambooks.com/                                                                |
|Amy Raphael                      |A Seat at the Table                                                          |Interviews with Women on the Frontline of Music’ Amy’s previous work has included interviews with Patti Smith and Courtney Love etc. In this book she talks to some amazing contemporary artists, inc Kate Tempest, Nadine Shah, Christine & the Queens. Important insights on every page.                                                                                                                        | https://twitter.com/Mr_Dave_Haslam | (DH)   | https://www.foxlanebooks.co.uk/                                                              |
|Carrie Brownstein                |Hunger Makes Me a Modern Girl: Carrie Brownstein                             |The guitarist of Sleater-Kinney writes about growing up in the Pacific Northwest in a dysfunctional family and then finding herself in ‘America’s best rock band’ (Greil Marcus) in the mid-90s. She is an excellent writer, but it’s the disarming honesty that pulls you in.                                                                                                                                    |https://twitter.com/amy_raphael     | (AR)   | https://www.forumbooksshop.com/                                                              |
|David Barnett and Martin Simmonds|Punk's Not Dead                                                              |A graphic novel that concerns the increasingly wild (and supernatural) adventures of a kid who finds himself best friends with Sid Vicious's ghost. But all is not what it seems. Set in the here and now, this captures punk's spirit. There are 2 volumes, and both are great.                                                                                                                                  |https://twitter.com/Beathhigh       | (IR)   | https://www.linghams.co.uk/                                                                  |
|David Cavanagh                   |My Magpie Eyes Are Hungry For The Prize                                      |Ostensibly about the rise and fall of Creation Records, but this being a book by the great David Cavanagh, it's about so much more than that. It's essential reading for anyone who wants to know how indie mutated from its anti-mainstream, anti-rockist, DIY outsider roots to the very opposite of all those things. A scintillating read.                                                                    |https://twitter.com/petepaphides    | (PP)   | http://www.claphambooks.com/                                                                 |
|David Hepworth                   |1971 – Never A Dull Moment                                                   |Beneath his imperturbably circumspect shell, Hepworth is as breathlessly enthusiastic a writer as his longtime sidekick Mark Ellen, and this book in which he argues that 1971 was the greatest ever year for music is the perfect vehicle for that enthusiasm. The chapter about Rod Stewart's rise to superstardom is worth the asking price alone.                                                             |https://twitter.com/petepaphides    | (PP)   | https://www.foxlanebooks.co.uk/                                                              |
|Kirstin Innes                    |Scabby Queen The rise and fall of a one-hit wonder                           |This novel focuses more on the main character's life after music, charting her political activism and various friendships. There are betrayals along the way, but it's a humane and searching story.                                                                                                                                                                                                              |https://twitter.com/Beathhigh       | (IR)   | https://blackwells.co.uk/bookshop/product/Scabby-Queen-by-Kirstin-Innes-author/9780008342296 |
|Lizzie Goodman                   |Meet Me in the Bathroom: Rebirth and Rock and Roll in New York City 2001-2011|This leave-no-stone-unturned oral history is pieced together with the voices of bands like the Strokes, Kings of Leon and Interpol, thus presenting multi-narrative memories of the same period. It’s often gossipy rather than reverent, but better for it.                                                                                                                                                      |https://twitter.com/amy_raphael     |(AR)    | http://www.city-books.co.uk/                                                                 |

and a the [ list of shops](data/book-shops-data.csv)

|Name              |Address            |Website                                 |type|
|------------------|-------------------|----------------------------------------|----|
|Blackwells        |various locations  | https://blackwells.co.uk/bookshop/home |    |
|Book-ish          |Crickhowell NP8 1BD| http://www.book-ish.co.uk/             |    |
|Clapham Books     |London, SW4 0JA    | http://www.claphambooks.com/           |    |
|Cover to Cover    |Swansea, SA3 4BQ   |  http://cover-to-cover.co.uk/          |    |
|David’s Bookshop  |Letchworth, SG6 3DE| https://www.davids-bookshops.co.uk/    |    |
|Ebb & Flo Bookshop|Chorley, PR7 2EJ   | http://ebbandflobookshop.co.uk/        |    |
|Edinburgh Bookshop|Edinburgh EH10 4DH | https://www.edinburghbookshop.com/     |    |
|Forum             |Corbridge, NE45 5AW|  https://www.forumbooksshop.com/       |    |
|Fox Lane Books    |pop-up locations   | https://www.foxlanebooks.co.uk/        |    |


## Replay

The [replay code](https://github.com/ajbrindle/listeningpartyreplay) is provided by [@andrewb1970](https://twitter.com/andrewb1970)

## Tools

TODO - explain the spring boot shell tool in the tools folder.  Basically takes the [csv data](data/time-slot-data.csv)
and generates the [upcoming-time-slots.html](snippets/upcoming-time-slots.html), [date-tbd-time-slots.html](snippets/date-tbd-time-slots.html) [all-time-slots.html](snippets/all-time-slots.html)
and the [completed-time-slots.html](snippets/completed-time-slots.html) from it as well as the [record store](snippets/record-stores.html)



A Lambda also exists that can be added to aws and triggered when the S3 bucket PUT's the time-slot-data.csv into it and writes
the files above back to the bucket (including the record stores which are derived from the record-store-data.csv)

## Tasks

- [x] Drive tabular data from file
- [ ] Document shell tool, write tests and link to [codefactor](https://www.codefactor.io)
- [x] Break up generation of html into upcoming, date tbc and archived
- [x] Add new archive page
- [x] Investigate twitter api for listening period date range for archive page
- [ ] Add shell command to preview index.html
- [ ] Add shell command to add, commit and push code
- [ ] Add command to call aws
- [x] Add TBC Page
- [x] Sort archive page latest to oldest
- [x] Add lambda to auto create html snippets from csv in github
- [x] Update lambda to invalidate the Cloud Front cache after writing the html to the s3 buckets
- [x] Redesign tables to use cardgroups
- [x] Move data csv files to a separate folder
- [ ] Show tweeters involved in listening party on index
- [ ] rejig menu/button options
- [ ] add each replay tweets to a twitter collection

