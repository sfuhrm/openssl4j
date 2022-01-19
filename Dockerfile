FROM openjdk:17-jdk-buster
RUN apt-get update && apt-get install make
COPY . openssl4j
RUN make
