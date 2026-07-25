FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY src /app/src
COPY data /app/data
RUN mkdir -p bin && javac -d bin -cp "src/main/java" src/main/java/com/airtribe/meditrack/api/MediTrackServer.java
EXPOSE 8080
CMD ["java", "-cp", "bin", "com.airtribe.meditrack.api.MediTrackServer"]
