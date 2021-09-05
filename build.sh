#!/usr/bin/env bash

mkdir -p release/bin

javac -d release/bin -g $(find nwcscore/src -name \*.java)
jar -c -f release/nwcscore.jar -e net.cadrian.nwcscore.Nwc2Ly -C release/bin net
