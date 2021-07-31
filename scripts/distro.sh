#!/bin/bash -eu
set -o pipefail

echo "Distro: Start "
SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
source "$SCRIPT_DIR/config.sh"

# First parameter is 'OS', if missing use `uname`
OS=${1:-$(uname)}
echo "OS: '$OS'"

# Install files
"$SCRIPT_DIR/install.sh"

mkdir -p "$DISTRO_DIR"
cd "$BDS_HOME/.."
tar -cvzf "$DISTRO_DIR/bds_$OS.tar.gz" $(basename $BDS_HOME)

echo "Distro: End"
