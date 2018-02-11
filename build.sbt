name := "lykke-waves-common"

organization := "ru.tolsi"

version in ThisBuild := {
  if (git.gitCurrentTags.value.nonEmpty) {
    git.gitDescribedVersion.value.get
  } else {
    if (git.gitHeadCommit.value.contains(git.gitCurrentBranch.value)) {
      git.gitHeadCommit.value.get.take(8) + "-SNAPSHOT"
    } else {
      git.gitCurrentBranch.value + "-" + git.gitHeadCommit.value.get.take(8) + "-SNAPSHOT"
    }
  }
}

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.wavesplatform" % "wavesj" % "0.3",
  "de.heikoseeberger" %% "akka-http-play-json" % "1.20.0-RC1",
  "org.scorexfoundation" %% "scrypto" % "1.2.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.3.0-alpha3",
  "org.slf4j" % "slf4j-api" % "1.8.0-beta1",
  "com.github.salat" %% "salat" % "1.11.2",
  "org.mongodb" %% "casbah" % "3.1.1",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test,
  "com.github.fakemongo" % "fongo" % "2.1.0" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

scalaVersion := "2.12.4"