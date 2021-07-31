#!/bin/bash -eu
set -o pipefail

echo "Install: Start"
SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
source "$SCRIPT_DIR/config.sh"

cd "$PROJECT_DIR"

# Build bds
"$SCRIPT_DIR/make.sh"

# Create 'bds' dir
mkdir -p "$BDS_HOME" 2> /dev/null || true

# Copy files to install dir
cp -vf "$BDS_BIN" "$BDS_HOME/"

# Copy default config file
echo
echo "Copying config file (do not overwrite)"
cp -vn config/bds.config "$BDS_HOME/bds.config" || true

# Copy 'include' dir
echo
echo "Copying include files"
cd "$PROJECT_DIR/config"
cp -rvf include "$BDS_HOME"

# Copy 'cluster*' files
echo
echo "Copying cluster* files (do not overwrite)"
cd "$PROJECT_DIR/config"
cp -rvn cluster* "$BDS_HOME" || true

echo "Install: End"

