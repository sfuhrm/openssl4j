FROM debian:10

ENV JAVA_PACKAGE=openjdk-11-jdk-headless
ARG JDK_URL

RUN apt-get update && apt-get install -y \
tar curl make gcc libssl1.1 libssl-dev
RUN mkdir jdk && cd jdk && curl --insecure --location ${JDK_URL} -o- | tar --strip-components=1 -xzvf-
COPY . openssl4j
RUN cd openssl4j && \
export JAVA_HOME=/jdk && \
export OS_ARCH=$(${JAVA_HOME}/bin/java build-helper/OsArch.java) && \
echo "JAVA_HOME    is ${JAVA_HOME}" && \
echo "OS_ARCH      is ${OS_ARCH}" && \
make
RUN cd openssl4j/target && ls -al
