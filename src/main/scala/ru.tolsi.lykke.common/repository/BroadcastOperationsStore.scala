package ru.tolsi.lykke.common.repository

import salat.annotations.Key

import scala.concurrent.Future

case class BroadcastOperation(@Key("_id") operationId: String, transactionId: String, signedTransaction: String)

trait BroadcastOperationsStore {
  def addBroadcastOperation(operation: BroadcastOperation): Future[Boolean]

  def findOperationIdByTransactionId(transactionId: String): Future[Option[String]]

  def findBroadcastOperationByOperationId(transactionId: String): Future[Option[BroadcastOperation]]

  def removeBroadcastOperation(id: String): Future[Boolean]
}
