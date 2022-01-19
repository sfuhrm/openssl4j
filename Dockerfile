FROM debian:11
ENV JAVA_PACKAGE=openjdk-17-jdk-headless
RUN apt-get update && apt-get install -y \
make gcc libssl1.1 libssl-dev $JAVA_PACKAGE maven
COPY . openssl4j
RUN cd openssl4j && \
export JAVA_HOME=$(apt-file show $JAVA_PACKAGE | cut -d" " -f2 | cut -d"/" -f1-5 | head -n1) && \
echo "JAVA_PACKAGE is ${JAVA_PACKAGE}" && \
echo "JAVA_HOME    is ${JAVA_HOME}" && \
make
