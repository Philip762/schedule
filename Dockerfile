FROM maven:3-amazoncorretto-21-alpine AS build

# setup + copy project files
ENV HOME=/usr/app
RUN mkdir -p "$HOME"
WORKDIR $HOME
COPY . .

# grant permission to execute maven in container and build project
RUN chmod +x ./mvnw && ./mvnw -DskipTests -f "$HOME"/pom.xml clean package

FROM eclipse-temurin:21-alpine
COPY --from=build /usr/app/target/*.jar /app/runner.jar
EXPOSE 8080

# start Springboot
ENTRYPOINT ["java", "-jar", "/app/runner.jar"]