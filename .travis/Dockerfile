#
# Debian, AdoptOpenJDK, Make, Maven
#

FROM debian:stretch
ENV OPENJDK_MAJOR_VERSION=11

# first update package info
RUN apt-get update && \
apt-get install --no-install-recommends -y apt-transport-https ca-certificates wget gnupg2 software-properties-common make gcc openssl && \
(wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | apt-key add - ) && \
add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/ && \
apt-get update && \
apt-get install  --no-install-recommends -y adoptopenjdk-${OPENJDK_MAJOR_VERSION}-hotspot && \
echo "export JAVA_HOME=$(cd $(dirname $(readlink -f "/etc/alternatives/java"))/../..; pwd)" >> /etc/profile.d/java_home.sh && \
echo "export PATH=$JAVA_HOME/bin:$PATH" >> /etc/profile.d/java_home.sh && \
ln -s $(cd $(dirname $(readlink -f "/etc/alternatives/java"))/../..; pwd) /jdk && \
rm -rf /var/lib/apt/lists/* && \
rm -rf /var/cache/apt/archives/*
WORKDIR /

CMD bash
