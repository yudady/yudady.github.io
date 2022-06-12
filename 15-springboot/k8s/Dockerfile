FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY ./build/libs/*.jar app.jar

#run the spring boot application
ENTRYPOINT ["java","-jar","/app/app.jar"]



