#!/bin/bash -eu
set -o pipefail

echo "Build bds documentation: Start"
SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
source "$SCRIPT_DIR/config.sh"

cd "$PROJECT_DIR/src/manual"
mkdocs build

echo "Build bds documentation: End"
