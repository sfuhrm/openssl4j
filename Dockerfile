FROM debian:11
RUN apt-get update && apt-get install -y \
make gcc libssl1.1 libssl-dev openjdk-17-jdk-headless maven
COPY . openssl4j
RUN cd openssl4j && make
