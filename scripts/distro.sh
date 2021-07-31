#!/bin/bash -eu
set -o pipefail

echo "Distro: Start "
pwd -P
SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
echo "DIR: 'SCRIPT_DIR'"
source "SCRIPT_DIR/config.sh"

# Install files
"$SCRIPT_DIR/install.sh"

mkdir -p "$DISTRO_DIR"
cd "$BDS_HOME"
tar -cvzf "$DISTRO_DIR/bds_$(uname).tgz" bds bds.config include clusterGeneric

echo "Distro: End"
