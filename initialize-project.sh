#!/bin/bash
mvn clean &&
mvn install:install-file -Dfile='.\lib\xmpp.jar' -DgroupId='com.jade.xmpp' -DartifactId=jade-xmpp -Dversion='1.0' -Dpackaging=jar &&
mvn install -f pom.xml &&
mvn package
