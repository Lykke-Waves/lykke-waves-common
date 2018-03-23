# lykke-waves-common
Lykke Waves common library. It contains common classes for accessing data and interfaces and required to build the project modules.

# Installation

This library uses [sbt](https://www.scala-sbt.org/) for building:

```
sbt clean publishLocal
```

After that, the library will be available in the local ivy2 repository `~/.ivy2` for other sbt projects. 

# Testing

This library uses [sbt](https://www.scala-sbt.org/) for testing:

```
sbt clean test
```

# Docker compose

You can build and start all the lykke-waves modules using

```
docker-compose up -d
```
