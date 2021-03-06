package ru.tolsi.lykke.common.api

import java.io.IOException
import java.nio.charset.Charset

import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.io.IOUtils
import org.apache.http.HttpStatus
import org.apache.http.client.HttpClient
import org.apache.http.client.config.{CookieSpecs, RequestConfig}
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.config.SocketConfig
import org.apache.http.impl.client.HttpClientBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}
import scalaj.http.HttpResponse

object HttpClientUsage {
  implicit def scalajHttpRespToTry[T](httpResponse: HttpResponse[T]): Try[T] = {
    if (httpResponse.isSuccess) {
      Success(httpResponse.body)
    } else {
      Failure(new IOException(s"Code ${httpResponse.code}: ${httpResponse.body}"))
    }
  }
}

trait HttpClientUsage extends StrictLogging {

  protected val reqConfig: RequestConfig = RequestConfig.custom()
    .setConnectionRequestTimeout(30000)
    .setConnectTimeout(15000)
    .setSocketTimeout(15000)
    .setCookieSpec(CookieSpecs.STANDARD)
    .build()
  protected val httpClient: HttpClient = HttpClientBuilder.create()
    .setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).setSoReuseAddress(true).build())
    .setDefaultRequestConfig(reqConfig)
    .disableAutomaticRetries().build()
  protected var httpClientContext: HttpClientContext = HttpClientContext.create


  protected def retry(url: String, retryCount: Int = 0, e: Exception): Future[String] = if (retryCount < 5) {
    logger.error(s"Failed request '$url' error, sleep 30s and retry", e)
    Thread.sleep(30000)
    makeGetRequest(url, retryCount + 1)
  } else {
    Future.failed(new IOException(s"Request '$url' failed after $retryCount proxy switches and retries", e))
  }

  protected def makeGetRequest(url: String, retryCount: Int = 0, allowNotFound: Boolean = false): Future[String] = {
    val getRequest = new HttpGet(url)

    try
      Future.fromTry(Try {
        val httpResponse = httpClient.execute(getRequest, httpClientContext)
        var body = IOUtils.toString(httpResponse.getEntity.getContent, Charset.defaultCharset)
        val httpStatus = httpResponse.getStatusLine.getStatusCode

        httpResponse.getEntity.getContent.close()
        httpResponse.asInstanceOf[CloseableHttpResponse].close()
        if (httpStatus != HttpStatus.SC_OK) {
          if (!allowNotFound) {
            throw new IOException(s"Code: $httpStatus, body: '$body'")
          } else {
            body = "404"
          }
        }
        body
      }).recoverWith {
        case e: IOException =>
          retry(url, retryCount, e)
      } andThen {
        case Success(body) =>
          logger.trace(s"Success request '$url', body: $body")
      }
    catch {
      case e: IOException =>
        retry(url, retryCount, e)
    }
  }
}
