package ru.tolsi.lykke.common.api

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import scorex.crypto.encode.Base58

object WavesTransaction {
  val IssueType = 3
  val TransferType = 4
}

object WavesTransferTransaction {
  implicit val wavesTransferTransactionReads = (
    (JsPath \ "id").read[String] and
      (JsPath \ "type").read[Int] and
      (JsPath \ "sender").read[String] and
      (JsPath \ "senderPublicKey").read[String] and
      (JsPath \ "recipient").read[String] and
      (JsPath \ "amount").read[Long] and
      (JsPath \ "assetId").readNullable[String] and
      (JsPath \ "timestamp").read[Long] and
      (JsPath \ "fee").read[Long] and
      (JsPath \ "feeAssetId").readNullable[String] and
      (JsPath \ "attachment").readNullable[String].map(b => if (b.nonEmpty && b.exists(_.nonEmpty)) Base58.decode(b.get).get else Array.emptyByteArray) and
      (JsPath \ "signature").read[String] and
      (JsPath \ "height").readNullable[Int]
    ) { (id: String, t: Int, sender: String, senderPublicKey: String, recipient: String, amount: Long, assetId: Option[String],
         timestamp: Long, fee: Long, feeAssetId: Option[String], attachment: Array[Byte], signature: String, height: Option[Int]) => {
    require(t == WavesTransaction.TransferType)
    new WavesTransferTransaction(id, sender, senderPublicKey, recipient, amount, assetId, timestamp, fee, feeAssetId, attachment, signature, height)
  }
  }
}

sealed trait WavesTransaction {
  def id: String
  def height: Option[Int] = None
}

case class WavesTransferTransaction(override val id: String,
                                    from: String,
                                    fromPublicKey: String,
                                    to: String,
                                    amount: Long,
                                    assetId: Option[String],
                                    timestamp: Long,
                                    fee: Long,
                                    feeAssetId: Option[String],
                                    attachment: Array[Byte],
                                    signature: String,
                                    override val height: Option[Int]) extends WavesTransaction

object WavesIssueTransaction {
  implicit val wavesIssueTransactionReads = (
    (JsPath \ "id").read[String] and
      (JsPath \ "type").read[Int] and
      (JsPath \ "sender").read[String] and
      (JsPath \ "senderPublicKey").read[String] and
      (JsPath \ "quantity").read[Long] and
      (JsPath \ "decimals").read[Byte] and
      (JsPath \ "timestamp").read[Long] and
      (JsPath \ "fee").read[Long] and
      (JsPath \ "name").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "signature").read[String] and
      (JsPath \ "height").readNullable[Int]
    ) { (id: String, t: Int, sender: String, senderPublicKey: String, amount: Long, decimals: Byte, timestamp: Long, fee: Long, name: String, description: String, signature: String, height: Option[Int]) => {
    require(t == WavesTransaction.IssueType)
    new WavesIssueTransaction(id: String, sender, senderPublicKey, amount, decimals, timestamp, fee, name, description, signature, height)
  }
  }
}

case class WavesIssueTransaction(override val id: String,
                                 from: String,
                                 fromPublicKey: String,
                                 amount: Long,
                                 decimals: Byte,
                                 timestamp: Long,
                                 fee: Long,
                                 name: String,
                                 description: String,
                                 signature: String,
                                 override val height: Option[Int]) extends WavesTransaction