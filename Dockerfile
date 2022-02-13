FROM openjdk:17-jdk

RUN apt-get update && apt-get install -y \
make gcc libssl1.1 libssl-dev
COPY . openssl4j
ENV JAVA_HOME=/opt/java/openjdk/
RUN echo "JAVA_HOME    is ${JAVA_HOME}"
RUN echo "OS_ARCH      is $(cd openssl4j/build-helper && ${JAVA_HOME}/bin/java -Xint OsArch.java)"
RUN cd openssl4j && \
make
RUN cd openssl4j/target && ls -al
