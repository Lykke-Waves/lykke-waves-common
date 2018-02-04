package ru.tolsi.lykke.common

import java.nio.ByteBuffer

import com.wavesplatform.wavesj.Asset.WAVES
import com.wavesplatform.wavesj.{Asset, Base58}
import org.bouncycastle.crypto.Digest
import org.bouncycastle.crypto.digests.Blake2bDigest

object UnsignedTransferTransaction {
  private val TRANSFER = 4.toByte
  private val MIN_BUFFER_SIZE = 120
}

case class UnsignedTransferTransaction(fromAddress: String, toAddress: String, amount: Long, assetId: Option[String], fee: Long, feeAssetId: Option[String], attachment: Option[String]) {

  import UnsignedTransferTransaction._

  //region Transaction ID calculation
  private val BLAKE2B256 = new Blake2bDigest(256)

  private def normalize(assetId: Option[String]) = assetId.filter(_.isEmpty).map(_ => Asset.WAVES).getOrElse(assetId.get)

  private def isWaves(assetId: Option[String]) = normalize(assetId) == WAVES

  private def hash(message: Array[Byte], ofs: Int, len: Int, alg: Digest) = {
    val res = new Array[Byte](alg.getDigestSize)
    alg.update(message, ofs, len)
    alg.doFinal(res, 0)
    res
  }

  private def idHash(message: Array[Byte], ofs: Int, len: Int) = hash(message, ofs, len, BLAKE2B256)

  private def putAsset(buffer: ByteBuffer, assetId: Option[String]): Unit = {
    if (isWaves(assetId)) buffer.put(0.toByte)
    else buffer.put(1.toByte).put(Base58.decode(assetId.get))
  }

  def id(senderPublicKey: Array[Byte]): TransactionId = {
    val attachmentBytes = attachment.getOrElse("").getBytes
    val datalen = (if (isWaves(assetId)) 0 else 32) + (if (isWaves(feeAssetId)) 0 else 32) + attachmentBytes.length + MIN_BUFFER_SIZE
    val timestamp = System.currentTimeMillis
    val buf = ByteBuffer.allocate(datalen)
    buf.put(TRANSFER).put(senderPublicKey)
    putAsset(buf, assetId)
    putAsset(buf, feeAssetId)
    buf.putLong(timestamp).putLong(amount).putLong(fee).put(Base58.decode(toAddress)).putShort(attachmentBytes.length.toShort).put(attachmentBytes)
    val toSign = buf.array
    val id = idHash(toSign, 0, toSign.length)
    TransactionId(Base58.encode(id))
  }

  //endregion
}
