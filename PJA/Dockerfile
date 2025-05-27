# Dockerfile (윈도우 사용자용 - Gradle 프로젝트 빌드된 JAR 실행)
FROM openjdk:17-jdk-slim

# JAR 복사
COPY build/libs/*SNAPSHOT.jar app.jar

# 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]
