package ru.tolsi.lykke.common.api

import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scalaj.http.Http

object WavesApi {
  def isTransferTransaction(js: JsObject): Boolean = {
    js.value.get("type").contains(JsNumber(4))
  }

  def isAssetIssue(js: JsObject): Boolean = {
    js.value.get("type").contains(JsNumber(3))
  }

  def parseOnlyTransfersAndIssuesFromJson(objs: Seq[JsObject]): Seq[WavesTransaction] = {
    val transfers = objs.filter(o => isTransferTransaction(o))
    val issues = objs.filter(o => isAssetIssue(o))
    val parsedTransfers = transfers.map(_.as[WavesTransferTransaction])
    val parsedIssues = issues.map(_.as[WavesIssueTransaction])
    parsedTransfers ++ parsedIssues
  }
}

class WavesApi(url: String) extends ScannerApi with HttpClientUsage {

  import WavesApi._

  def height: Future[Int] = {
    makeGetRequest(s"$url/blocks/height").map(js => Json.parse(js).as[JsObject].value("height").as[Int])
  }

  def balance(address: String): Future[Long] = {
    makeGetRequest(s"$url/addresses/balance/$address").map(js => Json.parse(js).as[JsObject].value("balance").as[Long])
  }

  def checkErrorStatus(js: JsObject): Try[JsObject] = {
    if (js.value.get("status").contains("error")) {
      Failure(new RuntimeException(js.value("details").as[String]))
    } else {
      Success(js)
    }
  }

  def blockAtHeight(h: Long): Future[Option[WavesBlock]] = {
    makeGetRequest(s"$url/blocks/at/$h")
      .map(js => checkErrorStatus(Json.parse(js).as[JsObject])
        .toOption
        .map(_.as[WavesBlock]))
  }

  def lastBlock: Future[WavesBlock] = {
    makeGetRequest(s"$url/blocks/last")
      .map(js => Json.parse(js)
        .as[WavesBlock])
  }

  def blocksByHeightRange(_from: Int, _to: Int): Future[Seq[WavesBlock]] = {
    val from = Math.min(_from, _to)
    val to = Math.max(_from, _to)
    val by = 99
    logger.debug(s"Starting loading waves blocks $from - $to by $by")
    val blocks = for {(from, to) <- RangeUtil.fromToSeq(from, to, by)} yield {
      {
        logger.debug(s"Loading waves blocks $from - $to by $by")
        makeGetRequest(s"$url/blocks/seq/$from/$to")
          .map(js => Json.parse(js).as[Seq[WavesBlock]])
      }
    }
    if (blocks.nonEmpty) Future.reduce(blocks.toIterable)(_ ++ _) else Future.successful(Seq.empty)
  }

  def blocksBySignature(signature: String): Future[Option[WavesBlock]] = {
    makeGetRequest(s"$url/blocks/signature/$signature").map(js => checkErrorStatus(Json.parse(js).as[JsObject])
      .toOption
      .map(_.as[WavesBlock]))
  }

  def utx(): Future[Seq[WavesTransaction]] = {
    makeGetRequest(s"$url/transactions/unconfirmed").map(js => {
      val arr = Json.parse(js).as[JsArray]
      parseOnlyTransfersAndIssuesFromJson(arr.as[Seq[JsObject]])
    })
  }

  def transactionInfo(id: String): Future[Option[WavesTransaction]] = {
    makeGetRequest(s"$url/transactions/info/$id", allowNotFound = true).map(js =>
      if (js == "404") {
        None
      } else {
        checkErrorStatus(Json.parse(js).as[JsObject])
          .toOption
          .map(_.as[JsObject])
          .flatMap(o => parseOnlyTransfersAndIssuesFromJson(Seq(o)).headOption)
      })
  }

  def sendSignedTransaction(txJson: String): Future[Unit] = {
    import HttpClientUsage.scalajHttpRespToTry
    Future.fromTry(Http(s"$url/assets/broadcast/transfer").header("content-type", "application/json").postData(txJson).asString.map(_ => ()))
  }
}
