FROM debian:10
RUN apt-get update && apt-get install make
RUN apt-cache search openjdk maven
COPY . openssl4j
RUN make
