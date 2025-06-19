# Dockerfile (윈도우 사용자용 - Gradle 프로젝트 빌드된 JAR 실행)
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# JAR 복사
COPY build/libs/*SNAPSHOT.jar app.jar

# 볼륨 설정: 외부 shared_logs를 마운트하기 위해
VOLUME /app/shared_logs

# 실행 명령
#ENTRYPOINT ["java", "-jar", "/app.jar"]
ENTRYPOINT ["java", "-Dlog.path=${LOG_PATH:/app/shared_logs/user-actions.json}", "-jar", "app.jar"]