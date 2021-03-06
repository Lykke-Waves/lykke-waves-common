package ru.tolsi.lykke.common

import java.nio.ByteBuffer

import com.wavesplatform.wavesj.Asset.WAVES
import com.wavesplatform.wavesj._
import org.bouncycastle.crypto.Digest
import org.bouncycastle.crypto.digests.Blake2bDigest
import org.whispersystems.curve25519.Curve25519
import play.api.libs.json.{Json, Reads, Writes}
import scorex.crypto.hash.Blake2b256

import scala.util.Try

object UnsignedTransferTransaction {
  private val TRANSFER = 4.toByte
  private val MIN_BUFFER_SIZE = 87

  // for id calculation
  private val BLAKE2B256 = new Blake2bDigest(256)

  // for signing
  private val cipher = Curve25519.getInstance(Curve25519.BEST)

  private def normalize(assetId: Option[String]) = assetId match {
    case Some(assetId) => assetId
    case None => Asset.WAVES
  }

  private def isWaves(assetId: Option[String]) = normalize(assetId) == WAVES

  private def hash(message: Array[Byte], ofs: Int, len: Int, alg: Digest) = {
    val res = new Array[Byte](alg.getDigestSize)
    alg.update(message, ofs, len)
    alg.doFinal(res, 0)
    res
  }

  private def idHash(message: Array[Byte]) = Blake2b256.hash(message)

  private def putAsset(buffer: ByteBuffer, assetId: Option[String]): Unit = {
    if (isWaves(assetId)) buffer.put(0.toByte)
    else buffer.put(1.toByte).put(Base58.decode(assetId.get))
  }

  def fromJsonString(string: String): Try[UnsignedTransferTransaction] = {
    Json.parse(string).validate[UnsignedTransferTransaction].asEither.left.map(errorsSeq => {
      new NoSuchElementException(errorsSeq.map(e => e._1.toString() -> e._2.map(_.message).mkString("[", ",", "]"))
        .mkString("[", ",", "]"))
    }).toTry
  }

  implicit val unsignedTransferTransactionReads: Reads[UnsignedTransferTransaction] = Json.reads[UnsignedTransferTransaction]
  implicit val unsignedTransferTransactionWrites: Writes[UnsignedTransferTransaction] = Json.writes[UnsignedTransferTransaction]
}

case class UnsignedTransferTransaction(fromAddress: String, toAddress: String, amount: Long, fee: Long,
                                       timestamp: Long, assetId: Option[String] = None, feeAssetId: Option[String] = None,
                                       attachment: Option[String] = None) {

  import UnsignedTransferTransaction._

  //region Transaction ID calculation

  def buildToSignData(senderPublicKey: PublicKeyAccount): Array[Byte] = {
    val attachmentBytes = attachment.getOrElse("").getBytes
    val datalen = (if (isWaves(assetId)) 0 else 32) + (if (isWaves(feeAssetId)) 0 else 32) + attachmentBytes.length + MIN_BUFFER_SIZE
    val buf = ByteBuffer.allocate(datalen)
    buf.put(TRANSFER).put(senderPublicKey.getPublicKey)
    putAsset(buf, assetId)
    putAsset(buf, feeAssetId)
    buf.putLong(timestamp).putLong(amount).putLong(fee).put(Base58.decode(toAddress)).putShort(attachmentBytes.length.toShort).put(attachmentBytes)
    buf.array
  }

  def id(sender: PublicKeyAccount): TransactionId = {
    val toSignData = buildToSignData(sender)
    val id = idHash(toSignData)
    TransactionId(Base58.encode(id))
  }

  //endregion


  def toJsonString: String = {
    Json.toJson(this).toString()
  }

  def signTransaction(account: PrivateKeyAccount): Transaction = {
    Transaction.makeTransferTx(account, toAddress, amount, assetId.orNull, fee, feeAssetId.orNull, attachment.orNull, timestamp)
  }
}
