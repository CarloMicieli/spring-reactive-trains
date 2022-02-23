# Spring Reactive Trains

[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)]

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![GitHub last commit](https://img.shields.io/github/last-commit/CarloMicieli/spring-reactive-trains)
![Build](https://github.com/CarloMicieli/spring-reactive-trains/workflows/Build/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/CarloMicieli/spring-reactive-trains/badge.svg?branch=main)](https://coveralls.io/github/CarloMicieli/spring-trains?branch=main)

A web api for model railway collections with `Spring Boot 2.4`.

## Requirements

- Java 17
- Docker and Docker compose

## How to run

To run the application using `gradle` or `docker`, a running postgres instance must be available on localhost.

This command will run **postgres** inside a docker container:

```bash
  docker run -it --rm -p:5432:5432 -e POSTGRES_PASSWORD=password -e POSTGRES_DB=sampledb postgres
```

To run the application using `gradle`:

```bash
  ./gradlew webapi:bootRun --args='--spring.profiles.active=local'
```

```bash
  ./gradlew clean bootBuildImage
  docker-compose up
```

### Docker compose

```
$ docker-compose build
$ docker-compose up -d
```

To stop the application:

```
$ docker-compose down
```
