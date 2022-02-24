# Spring Reactive Trains

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![GitHub last commit](https://img.shields.io/github/last-commit/CarloMicieli/spring-reactive-trains)
![Build](https://github.com/CarloMicieli/spring-reactive-trains/workflows/build/badge.svg)
[![codecov](https://codecov.io/gh/CarloMicieli/spring-reactive-trains/branch/main/graph/badge.svg?token=4Y0V0EL0V7)](https://codecov.io/gh/CarloMicieli/spring-reactive-trains)

A web api for model railway collections with `Spring Boot 2.6`.

## Requirements

- Java 17
- Docker and Docker compose

## How to run

To run the application using `gradle` or `docker`, a running postgres instance must be available on localhost.

This command will run **postgres** inside a docker container:

```bash
  docker run -it --rm -p:5432:5432 \
    -e POSTGRES_PASSWORD=password \
    -e POSTGRES_DB=trainsdb postgres
```

To run the application using `gradle`:

```bash
  ./gradlew webapi:bootRun --args='--spring.profiles.active=local'
```

```bash
  ./gradlew clean bootBuildImage
  docker-compose up
```

## Test the api

```bash
  pip install -U httpie
  pip install -U httpie-jwt-auth
```

Create a new user

```
  http POST :8080/auth/register username=user password=password
  HTTP/1.1 202 Accepted
  Authorization: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsiV...
```

in order to make api calls, it is required a JWT token. To get a token is required for the user to make a login:

```
  http POST :8080/auth/login username=george password=Stephenson
  HTTP/1.1 200 OK

  {
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwicm9sZXMiOlsidXNlc...
  }
```

```
  export JWT_AUTH_TOKEN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiO....
  http --auth-type=jwt :8080/auth
HTTP/1.1 200 
```

## Contributing

Contributions are always welcome!

See `contributing.md` for ways to get started.

Please adhere to this project's `code of conduct`.

### Conventional commits

This repository is following the conventional commits practice.

#### Enforcing using git hooks

```bash
  git config core.hooksPath .githooks
```

The hook itself can be found in `.githooks/commit-msg`.

#### Using Commitizen

Install [commitizen](https://github.com/commitizen-tools/commitizen)

```bash
  pip install commitizen
```

and then just use it

```bash
  cz commit
```

## License

[Apache 2.0](https://choosealicense.com/licenses/apache-2.0/)

```
   Copyright 2021 Carlo Micieli

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```