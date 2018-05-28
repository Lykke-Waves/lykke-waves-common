package ru.tolsi.lykke.common

import com.typesafe.scalalogging.StrictLogging
import com.wavesplatform.wavesj.PublicKeyAccount
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}
import scorex.crypto.encode.Base58

class UnsignedTransferTransactionSpec extends FunSuite with Matchers with BeforeAndAfterEach with StrictLogging {
  test("should generate valid id without fee asset and amount asset") {
    val sender = new PublicKeyAccount("4j7aKAQXagbFXDzvi1wPMVpWjBDvj9KEbKe9PnajAfAV", 'T')
    val tx = new UnsignedTransferTransaction(sender.getAddress, "3N58k2DtyKRJYPm2NJLNYi6uUYXQosbgALH", 220000, 100000, 1527267861070L)
    val bytes = tx.buildToSignData(sender)
    tx.id(sender).id shouldBe("8wGnkGgq5P1B88R41scEyfbyx9MDsrUBSfGDSq7w2JAK")
  }

  test("should generate valid id with the amount asset id") {
    val sender = new PublicKeyAccount("4j7aKAQXagbFXDzvi1wPMVpWjBDvj9KEbKe9PnajAfAV", 'T')
    val tx = new UnsignedTransferTransaction(sender.getAddress, "3N58k2DtyKRJYPm2NJLNYi6uUYXQosbgALH", 220000, 100000, 1527267861070L, assetId = Some("8sJLaTQ7NWUexDcF7Z5CmtXr1RpNt15ZQii6LZnChKmr"))
    val bytes = tx.buildToSignData(sender)
    tx.id(sender).id shouldBe("CfzTff39dpiZARQALktvbScFpAN4XLEwDqA78ThFcnu")
  }

  test("should generate valid id with the fee asset id") {
    val sender = new PublicKeyAccount("4j7aKAQXagbFXDzvi1wPMVpWjBDvj9KEbKe9PnajAfAV", 'T')
    val tx = new UnsignedTransferTransaction(sender.getAddress, "3N58k2DtyKRJYPm2NJLNYi6uUYXQosbgALH", 220000, 100000, 1527267861070L, feeAssetId = Some("8sJLaTQ7NWUexDcF7Z5CmtXr1RpNt15ZQii6LZnChKmr"))
    val bytes = tx.buildToSignData(sender)
    tx.id(sender).id shouldBe("ACvT5n9oMzycPXoCu5E1vzHrKCa1UquLwkG2PQmd79VU")
  }

  test("should generate valid id wit theh fee and amount asset ids") {
    val sender = new PublicKeyAccount("4j7aKAQXagbFXDzvi1wPMVpWjBDvj9KEbKe9PnajAfAV", 'T')
    val tx = new UnsignedTransferTransaction(sender.getAddress, "3N58k2DtyKRJYPm2NJLNYi6uUYXQosbgALH", 220000, 100000, 1527267861070L, assetId = Some("8sJLaTQ7NWUexDcF7Z5CmtXr1RpNt15ZQii6LZnChKmr"), feeAssetId = Some("8sJLaTQ7NWUexDcF7Z5CmtXr1RpNt15ZQii6LZnChKmr"))
    val bytes = tx.buildToSignData(sender)
    tx.id(sender).id shouldBe("6GACEhyrkcK2H3AgaquHcDDL18Jti9vBU15vgDwFi9Hs")
  }
}
