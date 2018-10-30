FROM openjdk:8
COPY ./out/production/DockerJavaApp/ /tmp
WORKDIR /tmp
ENTRYPOINT ["java","Slave"]