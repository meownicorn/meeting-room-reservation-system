FROM --platform=linux/amd64 openjdk:17-jdk-alpine
ADD target/meeting-room-reservation-system-0.0.1-SNAPSHOT.jar meeting-room-reservation-system-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/meeting-room-reservation-system-0.0.1-SNAPSHOT.jar"]