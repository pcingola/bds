#!/bin/sh -e

echo "Distro: Start "
pwd -P
echo "DIR: $(dirname $0)"
source "$(dirname $0)/config.sh"

# Install files
"$SCRIPT_DIR/install.sh"

mkdir -p "$DISTRO_DIR"
cd "$BDS_HOME"
tar -cvzf "$DISTRO_DIR/bds_$(uname).tgz" bds bds.config include clusterGeneric

echo "Distro: End"
