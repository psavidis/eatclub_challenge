FROM eclipse-temurin:24-jdk
COPY target/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]