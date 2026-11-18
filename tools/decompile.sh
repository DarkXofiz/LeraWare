#!/usr/bin/env bash
# Usage: tools/decompile.sh /path/to/NevaWare.jar /path/to/vineflower.jar
# Decompiles ONLY the project's own packages into src/main/java.
set -euo pipefail
JAR="${1:?original NevaWare.jar}"; VF="${2:?vineflower.jar}"
WORK="$(mktemp -d)"; trap 'rm -rf "$WORK"' EXIT
mkdir -p "$WORK/in" decompiled
# Keep: own code + bundled MIT mods + orbit-free. Drop: shaded netty (io/*) and orbit (meteordevelopment/*).
unzip -q "$JAR" 'neva/*' 'com/ezzenix/*' 'ru/donyka/*' -d "$WORK/in"
( cd "$WORK/in" && jar cf ../own.jar . )
java -jar "$VF" -dgs=1 -hdc=0 -rsy=1 -lit=1 "$WORK/own.jar" decompiled/
unzip -qo decompiled/own.jar -d src/main/java 2>/dev/null || cp -r decompiled/* src/main/java/
echo "Done. Next: ./gradlew build and fix compile errors WITHOUT changing logic."
