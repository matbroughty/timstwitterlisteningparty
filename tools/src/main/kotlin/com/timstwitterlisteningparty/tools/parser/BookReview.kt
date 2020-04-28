package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvBindByPosition
import org.apache.commons.lang3.StringUtils

data class BookReview(@CsvBindByPosition(position = 0) val author: String = "",
                      @CsvBindByPosition(position = 1) val title: String = "",
                      @CsvBindByPosition(position = 2) val description: String = "",
                      @CsvBindByPosition(position = 3) val reviewerTwitter: String = "",
                      @CsvBindByPosition(position = 4) val reviewerInitials: String = "",
                      @CsvBindByPosition(position = 5) val bookStoreWebsite: String = ""
) : HtmlRow {


  override fun buildHtmlRow(): String {

    return "  <div class=\"row d-flex mt-3\">\n" +
      "    <div class=\"card\" style=\"width: 100%;\">\n" +
      "      <div class=\"card-header font-weight-bold\">\n" +
      "        $author : $title\n" +
      "      </div>\n" +
      "      <div class=\"card-body\">\n" +
      "        <p class=\"card-text\">$description</p>\n" +
      "        <a class=\"btn btn-primary\"\n" +
      "           href=\"$bookStoreWebsite\"><i class=\"fas fa-laptop\"></i> Buy Here</a>\n" +
      "      </div>\n" +
      "      <div class=\"card-footer text-muted\">\n" +
      "        <a class=\"btn btn-light\" href=\"$reviewerTwitter\"><i\n" +
      "          class=\"fab fa-twitter\"> $reviewerInitials</i></a>\n" +
      "      </div>\n" +
      "    </div>\n" +
      "  </div>"

  }
}
