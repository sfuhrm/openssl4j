FROM debian:11
RUN apt-get update && apt-get install make openjdk-17-jdk-headless maven
COPY . openssl4j
RUN cd openssl4j && make
