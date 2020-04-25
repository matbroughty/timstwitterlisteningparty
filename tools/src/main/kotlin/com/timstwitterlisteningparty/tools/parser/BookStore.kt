package com.timstwitterlisteningparty.tools.parser

import com.opencsv.bean.CsvBindByPosition


data class BookStore(@CsvBindByPosition(position = 0)val name: String = "",
                     @CsvBindByPosition(position = 1)val address: String = "",
                     @CsvBindByPosition(position = 2)val webSite: String = "",
                     @CsvBindByPosition(position = 3)val type : String = "") : HtmlRow {



  /**
   * When converting back to an html row this will do the biz
   */
  override fun buildHtmlRow(): String {

    val webSiteButton = if(webSite.isBlank()){
      "class=\"pure-button-disabled\""
    } else "class=\"pure-button\""



    /**
     * The row html to populate
     * @see buildHtmlRow
     */
    return  "                <tr>\n" +
      "                  <td>$name</td>\n" +
      "                  <td>$address</td>\n" +
      "                  <td><a $webSiteButton \n" +
      "                                     href=\"$webSite\"><i class=\"fas fa-laptop\"></i></a></td>\n" +
      "                </tr>"

  }

}
