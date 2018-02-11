package ru.tolsi.lykke.common.repository.mongo

import com.mongodb.casbah.MongoCollection
import ru.tolsi.lykke.common.repository.FromAddressTransactionsStore

class MongoFromAddressTransactionsStore(collection: MongoCollection, observationsCollection: MongoCollection) extends MongoAddressTransactionsStore(collection, observationsCollection, "fromAddress") with FromAddressTransactionsStore