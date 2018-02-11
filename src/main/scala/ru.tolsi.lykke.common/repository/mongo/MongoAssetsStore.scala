package ru.tolsi.lykke.common.repository.mongo

import com.mongodb.DuplicateKeyException
import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import ru.tolsi.lykke.common.repository.{Asset, AssetsStore}
import salat.dao.{SalatDAO, SalatInsertError}
import salat.global._

import scala.concurrent.Future

class MongoAssetsStore(collection: MongoCollection) extends AssetsStore {

  private object MongoAssetsDAO extends SalatDAO[Asset, String](collection)

  override def registerAsset(asset: Asset): Future[Unit] = Future.successful(
    try {
      MongoAssetsDAO.insert(asset)
    } catch {
      case r: SalatInsertError if r.getCause.isInstanceOf[DuplicateKeyException] =>
        ()
    })

  override def getAssets(take: Int, continuationId: Option[String] = None): Future[Seq[Asset]] = Future.successful {
    val cur = (continuationId match {
      case Some(continuationId) => MongoAssetsDAO.find(ref = MongoDBObject("_id" -> MongoDBObject("$gt" -> continuationId)))
      case None => MongoAssetsDAO.find(MongoDBObject())
    }).sort(orderBy = MongoDBObject("_id" -> 1))
      .limit(take)
    val list = try {
      cur.toList
    } finally {
      cur.close()
    }

    val result = if (continuationId.isEmpty) {
      AssetsStore.WavesAsset +: list.dropRight(1)
    } else list

    result
  }

  override def getAsset(assetId: String): Future[Option[Asset]] = Future.successful(if (assetId == "WAVES") {
    Some(AssetsStore.WavesAsset)
  } else {
    MongoAssetsDAO.findOneById(assetId)
  })
}
