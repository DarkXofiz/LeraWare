#!/usr/bin/env bash
set -o pipefail
# Newest Fabric API built for this Minecraft version (falls back to gradle.properties)
MC="$(grep '^minecraft_version=' gradle.properties | cut -d= -f2)"
FAPI="$(curl -fsSL https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml \
  | grep -o "<version>[^<]*+${MC}</version>" | sed -E 's/<[^>]+>//g' | tail -1)"
echo "Fabric API: ${FAPI:-<default from gradle.properties>}"
EXTRA=""
[ -n "$FAPI" ] && EXTRA="-Pfabric_version=$FAPI"
gradle build --no-daemon $EXTRA 2>&1 | tee build.log
rc=$?
{
echo "Fabric API: ${FAPI:-default}"
echo "Toplam hata: $(grep -c ' error: ' build.log)"
echo "<pre>"
grep ' error: ' build.log | sed -E 's/^.* error: //; s/[A-Za-z0-9_.$]*class_[0-9]+/class_N/g' | sort | uniq -c | sort -rn | head -25
echo "</pre>"
echo "<pre>"
grep ' error: ' build.log | sed -E 's|^.*/src/main/java/||; s/:[0-9]+: error:.*//' | sort | uniq -c | sort -rn | head -25
echo "</pre>"
} >> "$GITHUB_STEP_SUMMARY"
exit $rc
