# requirements
- java 11
- maven
- docker (if you want to run with postgres)

# install
```sh
mvn install
```

# run with h2 in memory
This has preloaded data, see resources/1-dml.sql file to see data
```sh
mvn spring-boot:run
```

# run with postgres
```sh
docker-compose up -d
export SPRING_PROFILES_ACTIVE=postgres # if you wan't to execute with postgres database
mvn spring-boot:run
```

# run with h2 in file
```sh
export SPRING_PROFILES_ACTIVE=h2-file # if you wan't to execute with h2 file
mvn spring-boot:run
```

# test
```sh
mvn test
```

# sonar report
```sh
# install sonarqube locally https://docs.sonarqube.org/latest/setup/install-server/
mvn clean verify sonar:sonar
```

# docs
See in /docs path
