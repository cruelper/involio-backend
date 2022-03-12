FROM openjdk:16
ADD build/libs/involio-0.0.1.jar involio-0.0.1.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "involio-0.0.1.jar"]