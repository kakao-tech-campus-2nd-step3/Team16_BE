# jdk21 Image Start
FROM openjdk:21-jdk

# 인자 정리 - Jar
ARG JAR_FILE=build/libs/*.jar

# 환경 설정 파일 복사
COPY src/main/resources/application-oauth.properties /app/config/application-oauth.properties

# jar File Copy
COPY ${JAR_FILE} app.jar

# 프로파일을 docker로 설정하고 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.location=classpath:/application.properties,/app/config/application-oauth.properties", "-jar", "app.jar"]
