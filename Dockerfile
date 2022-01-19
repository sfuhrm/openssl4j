FROM debian:10
RUN apt-get update && apt-get install make openjdk-17-jdk maven
COPY . openssl4j
RUN make
