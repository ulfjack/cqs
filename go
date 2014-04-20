#!/bin/bash

if [ "x$JAVA" == "x" ]; then
	JAVA="java"
fi
JAVA_FLAGS="-Djava.awt.headless=true -Xmx200m"

if $JAVA $JAVA_FLAGS -jar startup.jar start 2>&1 | tee -i Demo/Logs/cqs.log;
then
	exit 0
else
	exit 1
fi
