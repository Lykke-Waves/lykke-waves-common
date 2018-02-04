package ru.tolsi.lykke.common

object NetworkType {

  case object Test extends NetworkType

  case object Main extends NetworkType

}

sealed trait NetworkType
