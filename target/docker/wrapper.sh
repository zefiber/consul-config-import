#!/bin/bash
sleep 10

echo '#####Starting up harmony-core-config-seed project####'
java $JVM_ARGUMENTS -Djava.security.egd=file:/dev/./urandom -jar /harmony-core-config-seed.jar
