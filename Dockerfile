FROM openjdk:16
EXPOSE 8080
ADD target/
ENTRYPOINT ["java", "-jar", ""]