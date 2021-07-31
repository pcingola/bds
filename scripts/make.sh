#!/bin/bash -eu
set -o pipefail

echo "Build bds: Start"
SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
source "$SCRIPT_DIR/config.sh"

cd "$PROJECT_DIR"

echo "Clean old JAR and GO binary"
rm -f "$BDS_GO_BIN" "$BDS_JAR" || true

# Find dependencies and copy them to "lib"
mvn --batch-mode dependency:resolve dependency:copy-dependencies

# Build Jar file
echo Building JAR file
ant 

# Build go program
"$SCRIPT_DIR/make_go.sh"

# Build binary (go executable + JAR file)
cat "$BDS_GO_BIN" "$BDS_JAR" > "$BDS_BIN"
chmod a+x "$BDS_BIN"
echo "Bds executable: '$BDS_BIN'"

# Remove JAR file and go binary
rm "$BDS_GO_BIN" "$BDS_JAR"

echo "Build bds: End"
