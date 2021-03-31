#!/bin/bash -eu
set -o pipefail

echo "Build bds: Start"

# Delete old jar
SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.."; pwd -P)

BDS_JAR="$PROJECT_DIR/build/bds.jar"
BDS_BIN="$PROJECT_DIR/build/bds"
BDS_GO_BIN="$PROJECT_DIR/go/bds/bds"

cd "$PROJECT_DIR"

rm -f "$BDS_JAR" || true

# Make sure 'bin' dir exists
mkdir -p bin 

# Build Jar file
echo Building JAR file
ant 

# Build go program
"$SCRIPT_DIR/make_go.sh"

# Build binay (go executable + JAR file)
cat "$BDS_GO_BIN" "$BDS_JAR" > "$BDS_BIN"
chmod a+x "$BDS_BIN"
echo "Bds executable: '$BDS_BIN'"

# Remove JAR file
rm "$BDS_JAR"

echo "Build bds: Finished"
