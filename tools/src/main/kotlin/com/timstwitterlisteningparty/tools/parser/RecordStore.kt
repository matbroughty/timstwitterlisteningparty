package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvBindByPosition


data class RecordStore(@CsvBindByPosition(position = 0) val name: String = "",
                       @CsvBindByPosition(position = 1) val address: String = "",
                       @CsvBindByPosition(position = 2) val webSite: String = "",
                       @CsvBindByPosition(position = 3) val twitterLink: String = "")

