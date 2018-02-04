package ru.tolsi.lykke.common.api.blockchain

import ru.tolsi.lykke.common.{TransactionId, TransactionState}

import scala.concurrent.Future

trait Api {
  def getTransactionsState(transactions: Seq[TransactionId]): Future[Map[TransactionId, TransactionState]]
}

