name := "lykke-waves-common"

organization := "ru.tolsi"

version := "0.1"

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

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.wavesplatform" % "wavesj" % "0.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test
)

scalaVersion := "2.12.4"