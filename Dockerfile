# 1단계: 빌드 환경
FROM gradle:8.10.2-jdk21 AS builder
WORKDIR /app

# 프로젝트의 모든 파일을 복사
COPY . .

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle build -x test --no-daemon

# 2단계: 실행 환경
FROM amazoncorretto:21
WORKDIR /app

# 1단계에서 생성된 JAR 파일을 실행 환경으로 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 8080 노출
EXPOSE 8080

# 기본 Spring 프로파일 설정
ENV SPRING_PROFILES_ACTIVE=default

# JAR 파일 실행
ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]