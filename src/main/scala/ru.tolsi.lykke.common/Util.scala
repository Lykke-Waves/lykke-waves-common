package ru.tolsi.lykke.common

object Util {
  val isDebug: Boolean = java.lang.management.ManagementFactory.getRuntimeMXBean.
    getInputArguments.toString.indexOf("-agentlib:jdwp") > 0
}
