#! /bin/bash
cd /build
mkdir -p target
xmlstarlet sel -N p="http://maven.apache.org/POM/4.0.0" -t -v "/p:project/p:version/text()" pom.xml > target/pom.version
