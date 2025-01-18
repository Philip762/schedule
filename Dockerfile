FROM maven:3-amazoncorretto-21-alpine AS build

# setup + copy files
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . .

RUN chmod +x ./mvnw # grant permission to execute maven in container
RUN ./mvnw -f $HOME/pom.xml clean package # build project

FROM eclipse-temurin:21-alpine
EXPOSE 8080
COPY --from=build /usr/app/target/*.jar /app/runner.jar
# start spirngboot and set the active profile to prod
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app/runner.jar"]