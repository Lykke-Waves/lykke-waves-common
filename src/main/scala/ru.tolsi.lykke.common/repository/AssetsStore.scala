package ru.tolsi.lykke.common.repository

import salat.annotations.Key

import scala.concurrent.Future

case class Asset(@Key("_id") assetId: String, name: String, address: String, accuracy: Int)

object AssetsStore{
  val WavesAsset = Asset("WAVES", "Waves", "", 8)
}
trait AssetsStore {
  def registerAsset(asset: Asset): Future[Unit]

  def getAssets(take: Int, continuationId: Option[String]): Future[Seq[Asset]]

  def getAsset(assetId: String): Future[Option[Asset]]
}
