package ru.tolsi.lykke.common

sealed trait TransactionState
object InProgress extends TransactionState
object Completed extends TransactionState
object Failed extends TransactionState