FROM debian:11

ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:11 $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN apt-get update && apt-get install -y \
make gcc libssl1.1 libssl-dev libssl
COPY . openssl4j
ENV JAVA_HOME=/opt/java/openjdk/
RUN echo "JAVA_HOME    is ${JAVA_HOME}"
RUN echo "OS_ARCH      is $(cd openssl4j/build-helper && ${JAVA_HOME}/bin/java -Xint OsArch.java)"
RUN wget "https://github.com/openssl/openssl/releases/download/openssl-3.0.8/openssl-3.0.8.tar.gz" && tar -C build -xzf openssl-3.0.8.tar.gz
RUN (cd build/openssl-3.0.8 && ./Configure enable-fips && make && sudo make install)
RUN echo "OpenSSL header files: "
RUN find / -name "provider.h"
RUN find / -name "core.h"
RUN find / -name "libssl.so.*"
RUN cd openssl4j && \
make
RUN cd openssl4j/target && ls -al
