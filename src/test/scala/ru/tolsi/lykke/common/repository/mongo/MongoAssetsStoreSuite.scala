package ru.tolsi.lykke.common.repository.mongo

import com.github.fakemongo.Fongo
import com.mongodb.casbah.MongoCollection
import org.scalatest.{AsyncFunSuite, BeforeAndAfterEach, Matchers}
import ru.tolsi.lykke.common.repository.{Asset, AssetsStore}

import scala.concurrent.Future


class MongoAssetsStoreSuite extends AsyncFunSuite with Matchers with BeforeAndAfterEach {
  val fongo = new Fongo("test ru.tolsi.lykke.common.repository.mongo server")

  val dbName = "test"

  test("MongoAssetsStore.getAsset should return None if not exists") {
    val db = fongo.getDB(dbName)
    val store = new MongoAssetsStore(new MongoCollection(db.getCollection("assets")))

    store.getAsset("abc").map(_ shouldBe 'empty)
  }

  test("MongoAssetsStore.getAsset should return Some after register asset") {
    val db = fongo.getDB(dbName)
    val store = new MongoAssetsStore(new MongoCollection(db.getCollection("assets")))

    val asset = Asset("a1", "aaa", "bbb", 9)
    store.registerAsset(asset).flatMap(_ =>
      store.getAsset("a1").map(_ shouldBe Some(asset)))
  }

  test("MongoAssetsStore.getAssets should return correct sized lists") {
    val db = fongo.getDB(dbName)
    val store = new MongoAssetsStore(new MongoCollection(db.getCollection("assets")))

    val assets = (for {i <- 0 to 20} yield Asset("a" + i, "aaa", "bbb", 9)).sortBy(_.assetId)

    val resisterAssetsF = Future.sequence(assets.map(store.registerAsset))

    resisterAssetsF.flatMap(_ =>
      store.getAssets(10).map(_ shouldBe AssetsStore.WavesAsset +: assets.take(9)))
  }

  test("MongoAssetsStore.getAssets should return correct sized lists with continuation") {
    val db = fongo.getDB(dbName)
    val store = new MongoAssetsStore(new MongoCollection(db.getCollection("assets")))

    val assets = (for {i <- 0 to 20} yield Asset("a" + i, "aaa", "bbb", 9)).sortBy(_.assetId)

    val resisterAssetsF = Future.sequence(assets.map(store.registerAsset))

    resisterAssetsF.flatMap(_ =>
      store.getAssets(10).flatMap(l => {
        store.getAssets(10, Some(l.last.assetId)).map(_ shouldBe assets.slice(9, 19))
      }))
  }

  override protected def afterEach(): Unit = {
    fongo.dropDatabase(dbName)
  }
}