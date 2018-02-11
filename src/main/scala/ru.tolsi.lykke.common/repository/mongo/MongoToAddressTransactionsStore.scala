package ru.tolsi.lykke.common.repository.mongo

import com.mongodb.casbah.MongoCollection
import ru.tolsi.lykke.common.repository.ToAddressTransactionsStore

class MongoToAddressTransactionsStore(collection: MongoCollection, observationsCollection: MongoCollection) extends MongoAddressTransactionsStore(collection, observationsCollection, "toAddress") with ToAddressTransactionsStore
