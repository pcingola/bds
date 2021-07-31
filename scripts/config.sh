#!/bin/bash -eu
set -o pipefail

# Path variables
SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.."; pwd -P)
DISTRO_DIR="$PROJECT_DIR/distro"

echo "Paths:"
echo "  SCRIPT_DIR  : '$SCRIPT_DIR'"
echo "  PROJECT_DIR : '$PROJECT_DIR'"
echo "  DISTRO_DIR  : '$DISTRO_DIR'"

# BDS variables
if [ -z ${BDS_HOME:-} ]; then
    BDS_HOME="$HOME/.bds"
fi
BDS_JAR="$PROJECT_DIR/build/bds.jar"
BDS_BIN="$PROJECT_DIR/build/bds"
BDS_GO_BIN="$PROJECT_DIR/go/bds/bds"

echo "  BDS_HOME    : '$BDS_HOME'"
echo "  BDS_BIN     : '$BDS_BIN'"
echo "  BDS_JAR     : '$BDS_JAR'"
echo "  BDS_GO_BIN  : '$BDS_GO_BIN'"
