package com.timstwitterlisteningparty.tools.twitter

import com.timstwitterlisteningparty.tools.parser.TimeSlot
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

@Component
class TweetUtils() {


  private val logger = LoggerFactory.getLogger(javaClass)

  fun tweet(msg: String): String {
    var status = ""
    status = try {
      val cb = ConfigurationBuilder()
      val tf = TwitterFactory(cb.build())
      val twitter: Twitter = tf.instance
      twitter.updateStatus(msg).text
    } catch (e: Exception) {
      print("Some badness with sending $msg  as a tweet ${e.localizedMessage}")
      return e.localizedMessage
    }
    return status
  }


  fun tweetReplay(timeSlot: TimeSlot, replayLink: String): String {
    return tweet("Replay available ${timeSlot.tweeterList().first()} : ${timeSlot.band} : ${timeSlot.album} at $replayLink #TimsTwitterListeningParty")
  }

  fun createCollection(replayId: String) {
    val cb = ConfigurationBuilder()
    val tf = TwitterFactory(cb.build())
    val twitter: Twitter = tf.instance

    val statusResponse = twitter.getResponse("https://api.twitter.com/1.1/collections/show.json?id=custom-1255049376134823938")

    logger.info("http response $statusResponse")


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

  }


//  fun generateAuthorizationHeader(method: String, url: String, params: Array<HttpParameter?>, nonce: String, timestamp: String,
//                                  otoken: AccessToken, consumerKey: String, consumerSecret: String): String? {
//    var params = params
//    val oauthHeaderParams: MutableList<HttpParameter> = ArrayList(5)
//    oauthHeaderParams.add(HttpParameter("oauth_consumer_key", consumerKey))
//    oauthHeaderParams.add(HttpParameter("oauth_signature_method", "HMAC-SHA1"))
//    oauthHeaderParams.add(HttpParameter("oauth_timestamp", timestamp))
//    oauthHeaderParams.add(HttpParameter("oauth_nonce", nonce))
//    oauthHeaderParams.add(HttpParameter("oauth_version", "1.0"))
//    if (otoken != null) {
//      oauthHeaderParams.add(HttpParameter("oauth_token", otoken.token as String))
//    }
//    val signatureBaseParams: MutableList<HttpParameter> = ArrayList(oauthHeaderParams.size + params.size)
//    signatureBaseParams.addAll(oauthHeaderParams)
//    if (!HttpParameter.containsFile(params)) {
//      toParamList(params)?.let { signatureBaseParams.addAll(it) }
//    }
//    parseGetParameters(url, signatureBaseParams)
//    val base = StringBuilder(method).append("&")
//      .append(HttpParameter.encode(constructRequestURL(url))).append("&")
//    base.append(HttpParameter.encode(normalizeRequestParameters(signatureBaseParams)))
//    val oauthBaseString = base.toString()
//    logger.debug("OAuth base string: ", oauthBaseString)
//    val signature: String = generateSignature(oauthBaseString, otoken, consumerSecret)
//    logger.debug("OAuth signature: ", signature)
//    oauthHeaderParams.add(HttpParameter("oauth_signature", signature))
//
//
//    return "OAuth " + OAuthAuthorization.encodeParameters(oauthHeaderParams, ",", true)
//  }
//
//  fun toParamList(params: Array<HttpParameter?>): List<HttpParameter>? {
//    val paramList: MutableList<HttpParameter> = ArrayList(params.size)
//    paramList.addAll(params.toCollection())
//    return paramList
//  }
//
//  private fun parseGetParameters(url: String, signatureBaseParams: MutableList<HttpParameter>) {
//    val queryStart = url.indexOf("?")
//    if (-1 != queryStart) {
//      val queryStrs = url.substring(queryStart + 1).split("&").toTypedArray()
//      try {
//        for (query in queryStrs) {
//          val split = query.split("=").toTypedArray()
//          if (split.size == 2) {
//            signatureBaseParams.add(
//              HttpParameter(URLDecoder.decode(split[0],
//                "UTF-8"), URLDecoder.decode(split[1],
//                "UTF-8"))
//            )
//          } else {
//            signatureBaseParams.add(
//              HttpParameter(URLDecoder.decode(split[0],
//                "UTF-8"), "")
//            )
//          }
//        }
//      } catch (ignore: UnsupportedEncodingException) {
//      }
//    }
//  }
//
//
//  fun constructRequestURL(url: String): String? {
//    var url = url
//    val index = url.indexOf("?")
//    if (-1 != index) {
//      url = url.substring(0, index)
//    }
//    val slashIndex = url.indexOf("/", 8)
//    var baseURL = url.substring(0, slashIndex).toLowerCase()
//    val colonIndex = baseURL.indexOf(":", 8)
//    if (-1 != colonIndex) {
//      // url contains port number
//      if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
//        // http default port 80 MUST be excluded
//        baseURL = baseURL.substring(0, colonIndex)
//      } else if (baseURL.startsWith("https://") && baseURL.endsWith(":443")) {
//        // http default port 443 MUST be excluded
//        baseURL = baseURL.substring(0, colonIndex)
//      }
//    }
//    url = baseURL + url.substring(slashIndex)
//    return url
//  }
//
//  private fun normalizeRequestParameters(params: List<HttpParameter>): String? {
//    Collections.sort(params)
//    return encodeParameters(params)
//  }
//
//
//  fun encodeParameters(httpParams: List<HttpParameter>): String? {
//    return encodeParameters(httpParams, "&", false)
//  }
//
//  fun encodeParameters(httpParams: List<HttpParameter>, splitter: String?, quot: Boolean): String? {
//    val buf = java.lang.StringBuilder()
//    for (param in httpParams) {
//      if (!param.isFile && !param.isJson) {
//        if (buf.isNotEmpty()) {
//          if (quot) {
//            buf.append("\"")
//          }
//          buf.append(splitter)
//        }
//        buf.append(HttpParameter.encode(param.name)).append("=")
//        if (quot) {
//          buf.append("\"")
//        }
//        buf.append(HttpParameter.encode(param.value))
//      }
//    }
//    if (buf.isNotEmpty()) {
//      if (quot) {
//        buf.append("\"")
//      }
//    }
//    return buf.toString()
//  }
//
//
//  fun generateSignature(data: String, token: AccessToken, consumerSecret : String): String? {
//    var byteHMAC: ByteArray? = null
//    try {
//      val mac = Mac.getInstance(HMAC_SHA1)
//      var spec: SecretKeySpec?
//      if (null == token) {
//        val oauthSignature = HttpParameter.encode(consumerSecret) + "&"
//        spec = SecretKeySpec(oauthSignature.toByteArray(), HMAC_SHA1)
//      } else {
//        spec =  token.getSecretKeySpec()
//        if (null == spec) {
//          val oauthSignature = HttpParameter.encode(consumerSecret) + "&" + HttpParameter.encode(token.getTokenSecret())
//          spec = SecretKeySpec(oauthSignature.toByteArray(), HMAC_SHA1)
//          token.setSecretKeySpec(spec)
//        }
//      }
//      mac.init(spec)
//      byteHMAC = mac.doFinal(data.toByteArray())
//    } catch (ike: InvalidKeyException) {
//      logger.error("Failed initialize \"Message Authentication Code\" (MAC)", ike)
//      throw AssertionError(ike)
//    } catch (nsae: NoSuchAlgorithmException) {
//      logger.error("Failed to get HmacSHA1 \"Message Authentication Code\" (MAC)", nsae)
//      throw AssertionError(nsae)
//    }
//    return BASE64Encoder.encode(byteHMAC)
//  }


}

fun main() {
  println("Hello World!")
  TweetUtils().createCollection("")
}

