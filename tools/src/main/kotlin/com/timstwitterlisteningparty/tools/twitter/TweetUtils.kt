package com.timstwitterlisteningparty.tools.twitter

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

@Component
class TweetUtils(val builder: RestTemplateBuilder) {

  fun tweet(msg: String) : String{
    var status = ""
    try {
      val cb = ConfigurationBuilder()
      val tf = TwitterFactory(cb.build())
      val twitter: Twitter = tf.instance
      status = twitter.updateStatus(msg).text
    }catch(e: Exception){
      print("Some badness with sending $msg  as a tweet ${e.localizedMessage}")
      status = e.localizedMessage
    }
    return status
  }

  fun createCollection(replayId: String): String {
    val cb = ConfigurationBuilder()
    val tf = TwitterFactory(cb.build())
    val twitter: Twitter = tf.instance

    val status = twitter.updateStatus(replayId)
//    val token = (tf.instance.authorization as OAuthAuthorization).oAuthAccessToken.token
//
//    val restTemplate = builder.rootUri("https://api.twitter.com/1.1")
//      .defaultHeader("Authorization", "OAuth oauth_consumer_key=\"SwGclrCfooMnumqBFtgdudfnZ\",oauth_token=\"${token}\",oauth_signature_method=\"HMAC-SHA1\",oauth_timestamp=\"1588153481\",oauth_nonce=\"38GX8c7i6Mx\",oauth_version=\"1.0\",oauth_signature=\"fTwKuEKpn7lW0j0Mps%2BGLh8Wi2Y%3D\"").build()
//
//    var json: String? = restTemplate.getForObject("/collections/show.json?id=custom-1255049376134823938", String::class.java)
//
//    print("json from collections is $json")
//
//
//    json = restTemplate.getForObject("/followers/ids.json", String::class.java)
//
//    print("json from followers is $json")

    return "$status is here"
  }

}


///*package*/
//fun generateAuthorizationHeader(method: String?, url: String?, params: Array<HttpParameter?>?, nonce: String?, timestamp: String?, otoken: AccessToken?): String? {
//  var params = params
//  if (null == params) {
//    params = arrayOfNulls(0)
//  }
//  val oauthHeaderParams: MutableList<HttpParameter> = ArrayList(5)
//  oauthHeaderParams.add(HttpParameter("oauth_consumer_key", consumerKey))
//  oauthHeaderParams.add(HttpParameter("oauth_signature_method", "HMAC-SHA1"))
//  oauthHeaderParams.add(HttpParameter("oauth_timestamp", timestamp))
//  oauthHeaderParams.add(HttpParameter("oauth_nonce", nonce))
//  oauthHeaderParams.add(HttpParameter("oauth_version", "1.0"))
//  if (otoken != null) {
//    oauthHeaderParams.add(HttpParameter("oauth_token", otoken.token))
//  }
//  val signatureBaseParams: MutableList<HttpParameter> = ArrayList(oauthHeaderParams.size + params.size)
//  signatureBaseParams.addAll(oauthHeaderParams)
//  if (!HttpParameter.containsFile(params)) {
//    signatureBaseParams.addAll(toParamList(params))
//  }
//  parseGetParameters(url, signatureBaseParams)
//  val base = StringBuilder(method).append("&")
//    .append(HttpParameter.encode(OAuthAuthorization.constructRequestURL(url))).append("&")
//  base.append(HttpParameter.encode(OAuthAuthorization.normalizeRequestParameters(signatureBaseParams)))
//  val oauthBaseString = base.toString()
//  OAuthAuthorization.logger.debug("OAuth base string: ", oauthBaseString)
//  val signature: String = generateSignature(oauthBaseString, otoken)
//  OAuthAuthorization.logger.debug("OAuth signature: ", signature)
//  oauthHeaderParams.add(HttpParameter("oauth_signature", signature))
//
//
//  return "OAuth " + OAuthAuthorization.encodeParameters(oauthHeaderParams, ",", true)
//}
//
//fun toParamList(params: Array<HttpParameter>): List<HttpParameter>? {
//  val paramList: MutableList<HttpParameter> = ArrayList(params.size)
//  paramList.addAll(listOf(*params))
//  return paramList
//}
//
//private fun parseGetParameters(url: String, signatureBaseParams: MutableList<HttpParameter>) {
//  val queryStart = url.indexOf("?")
//  if (-1 != queryStart) {
//    val queryStrs = url.substring(queryStart + 1).split("&").toTypedArray()
//    try {
//      for (query in queryStrs) {
//        val split = query.split("=").toTypedArray()
//        if (split.size == 2) {
//          signatureBaseParams.add(
//            HttpParameter(URLDecoder.decode(split[0],
//              "UTF-8"), URLDecoder.decode(split[1],
//              "UTF-8"))
//          )
//        } else {
//          signatureBaseParams.add(
//            HttpParameter(URLDecoder.decode(split[0],
//              "UTF-8"), "")
//          )
//        }
//      }
//    } catch (ignore: UnsupportedEncodingException) {
//    }
//  }
//
//
//
//}


