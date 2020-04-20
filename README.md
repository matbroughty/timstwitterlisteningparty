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

The time slot website data is driven by a simple csv file - which can be found [here](time-slot-data.csv) and is in the form:

|ISO Date Time   |Band                              |Album                          |Twitter Link                                               |
|----------------|----------------------------------|-------------------------------|-----------------------------------------------------------|
|2020-04-08T21:00|The Lovely Eggs                   |I am Moron                     |https://twitter.com/Tim_Burgess/status/1247643296740052992 |
|2020-04-08T22:00|The Flaming Lips                  |The Soft Bulletin              |https://twitter.com/Tim_Burgess/status/1244305878431522818 |
|2020-04-09T20:00|Sleaford Mods                     |Austerity Dogs                 |https://twitter.com/sleafordmods/status/1245357314619686912|
|2020-04-09T21:00|Pigs Pigs Pigs Pigs Pigs Pigs Pigs|Viscerals                      |https://twitter.com/Pigsx7/status/1244988772019773444      |
|2020-04-09T22:00|Orange Juice                      |You Cant Hide Your Love Forever|https://twitter.com/Tim_Burgess/status/1242598621893312515 |
|2020-04-10T20:00|British Sea Power                 |Open Season                    |https://twitter.com/BSPOfficial/status/1246855563184607232 |
|2020-04-10T21:00|Jane Weaver                       |Modern Kosmology               |https://twitter.com/JanelWeaver/status/1245800566732853248 |
|2020-04-10T22:00|Mogwai                            |Come On Die Young              |https://twitter.com/mogwaiband/status/1244223427239653381  |
|2020-04-11T21:00|Beta Band                         |The Three E.P.'s               |https://twitter.com/Tim_Burgess/status/1246378678101200896 |
|2020-04-11T22:00|Charlatans                        |Between 10th and 11th          |https://twitter.com/Tim_Burgess/status/1247592692219076616 |
|2020-04-12T21:00|Pulp                              |Different Class                |https://twitter.com/Tim_Burgess/status/1246506666901807106 |
|2020-04-12T22:00|New Order                         |Power Corruption & Lies        |https://twitter.com/Tim_Burgess/status/1244623119295352839 |

The record shop website data controlled another simple csv file - which can be found [here](record-store-data.csv) and is in the form:

|NAME            |ADDRESS                                                         |WEBSITE                                              |TWITTER                             |
|----------------|----------------------------------------------------------------|-----------------------------------------------------|------------------------------------|
|Creekside Vinyl |3 Market Street,Faversham, Kent ME13 7AH                        |https://creeksidevinyl.co.uk/                        |https://twitter.com/Creeksidevinyl  |
|Cliffs Margate  |172 Northdown Road Margate, England, CT9 2QN United Kingdom     |http://www.cliffsmargate.com                         |https://twitter.com/cliffsmargate   |
|Ben Oneill      |64 O'Connell Street, Dungarvan, Co. Waterford, Ireland, X35 HF54|http://www.benoneilldungarvan.com/                   | https://twitter.com/BenONeillRecord|
|Freebird Records|15a Wicklow Street Dublin                                       | https://freebirdrecords.com/                        |https://twitter.com/FreebirdRecords |
|Bella Union     |Online BN1 1AJ Brighton                                         |https://bellaunion.ochre.store/bella-union-vinyl-shop|https://twitter.com/VinylBella      |
|Spiller Records |27 The Morgan Arcade, CARDIFF CF10 1AF                          | http://spillersrecords.com/                         |https://twitter.com/spillersrecords |

This is also used to drive the [google map](https://drive.google.com/open?id=1XhFWnejDpNMuz2qG6iIOt5WIAdcEXFjX&usp=sharing)

## Replay

The [replay code](https://github.com/ajbrindle/listeningpartyreplay) is provided by [@andrewb1970](https://twitter.com/andrewb1970)

## Tools

TODO - explain the spring boot shell tool in the tools folder.  Basically takes the [csv data](time-slot-data.csv)
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
- [ ] Redesign tables to use cardgroups
- [ ] Move data csv files to a separate folder

