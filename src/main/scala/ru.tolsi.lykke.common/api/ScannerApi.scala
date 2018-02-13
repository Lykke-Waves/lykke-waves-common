package ru.tolsi.lykke.common.api

import scala.concurrent.Future

trait ScannerApi {
  def height: Future[Int]

  def blocksByHeightRange(from: Int, to: Int): Future[Seq[WavesBlock]]
}