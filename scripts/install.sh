#!/bin/bash -eu
set -o pipefail

if [ -z ${BDS_HOME:-} ]; then
    BDS_HOME="$HOME/.bds"
	echo "Setting BDS_HOME='$BDS_HOME'"
fi

SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.."; pwd -P)
BDS_BIN="$PROJECT_DIR/build/bds"

echo "Changing dir" `dirname $0`

# This script must be run from the parent directory 
cd "$PROJECT_DIR"

# Build bds
"$SCRIPT_DIR/make.sh"


# Create 'bds' dir
mkdir -p "$BDS_HOME" 2> /dev/null || true

# Copy files to install dir
cp -vf "$BDS_BIN" "$BDS_HOME/"

# Copy 'include' dir
echo
echo "Copying include files"
cd "$PROJECT_DIR"
cp -rvf include "$BDS_HOME"

if [ ! -e "$BDS_HOME/bds.config" ]
then
	echo "Copying default config file"
	cp config/bds.config "$BDS_HOME/bds.config"
fi

echo "Done!"
