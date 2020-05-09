package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvBindByPosition

data class BookReview(@CsvBindByPosition(position = 0) val author: String = "",
                      @CsvBindByPosition(position = 1) val title: String = "",
                      @CsvBindByPosition(position = 2) val description: String = "",
                      @CsvBindByPosition(position = 3) val reviewerTwitter: String = "",
                      @CsvBindByPosition(position = 4) val reviewerInitials: String = "",
                      @CsvBindByPosition(position = 5) val bookStoreWebsite: String = "")

