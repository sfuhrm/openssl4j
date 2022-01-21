FROM debian:10

ENV JAVA_PACKAGE=openjdk-11-jdk-headless
ARG JDK_URL

RUN apt-get update && apt-get install -y \
tar curl make gcc libssl1.1 libssl-dev
RUN mkdir jdk && cd jdk && curl --insecure --location ${JDK_URL} -o- | tar --strip-components=1 -xzvf-
COPY . openssl4j
ENV JAVA_HOME=/jdk
RUN echo "JAVA_HOME    is ${JAVA_HOME}"
RUN echo "OS_ARCH      is $(cd build-helper && ${JAVA_HOME}/bin/java OsArch.java)"
RUN cd openssl4j && \
make
RUN cd openssl4j/target && ls -al
