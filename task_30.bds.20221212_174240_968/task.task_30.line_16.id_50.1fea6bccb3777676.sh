#!/bin/bash -eu
set -o pipefail

cd '/Users/kqrw311/workspace/bds'

# SYS command. line 17
invalid_command test/unit/task/task_30.out_1.txt > test/unit/task/task_30.out_2.txt
# Checksum: 4e84e5c8
