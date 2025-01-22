FROM maven:3-amazoncorretto-21-alpine AS build

# setup + copy files
ENV HOME=/usr/app
RUN mkdir -p "$HOME"
WORKDIR $HOME
COPY . .

# grant permission to execute maven in container
RUN chmod +x ./mvnw && ./mvnw -DskipTests -f "$HOME"/pom.xml clean package

FROM eclipse-temurin:21-alpine
EXPOSE 8080
COPY --from=build /usr/app/target/*.jar /app/runner.jar
# start spirngboot and set the active profile to prod
ENTRYPOINT ["java", "-jar", "/app/runner.jar"]

