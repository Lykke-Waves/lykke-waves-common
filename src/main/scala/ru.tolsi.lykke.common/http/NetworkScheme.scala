package ru.tolsi.lykke.common.http

import ru.tolsi.lykke.common.NetworkType

object NetworkScheme {
  val MainnetScheme = 'W'
  val TestnetScheme = 'T'
}

trait NetworkScheme {

  import NetworkScheme._

  def networkType: NetworkType

  protected val scheme: Char = if (networkType == NetworkType.Main) MainnetScheme else TestnetScheme
}
