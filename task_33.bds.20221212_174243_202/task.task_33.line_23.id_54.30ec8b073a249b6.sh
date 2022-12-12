#!/bin/bash -eu
set -o pipefail

cd '/Users/kqrw311/workspace/bds'

# SYS command. line 24
invalid_command test/unit/task/task_33.out_1.txt > test/unit/task/task_33.out_2.txt
# Checksum: f84dc98c
