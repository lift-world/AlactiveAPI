FROM eclipse-temurin:17-jdk-focal

ARG JAR=build/libs/\*.jar
COPY $JAR app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]