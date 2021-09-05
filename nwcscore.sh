#!/usr/bin/env bash

for f in "$@"; do
    o="${f%.nwctxt}.ly"
    echo "$f -> $o"
    java -jar "$(dirname $(readlink -f "$0"))/release/nwcscore.jar" "$f" || exit 1
    lilypond "$o" || exit 1
done
