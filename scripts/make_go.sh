#!/bin/bash -eu
set -o pipefail

echo "Build bds go: Start"

SCRIPT_DIR=$(cd $(dirname $0); pwd -P)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.."; pwd -P)

# Make sure 'bin' dir exists
mkdir -p bin

# Build go program
echo
echo Building bds wrapper: Compiling GO program
cd "$PROJECT_DIR/go/bds/"
export GOPATH=`pwd`
export GO111MODULE=auto

# Make sure dependencies are downlaoded
go get -v github.com/aws/aws-sdk-go/aws
go get -v github.com/aws/aws-sdk-go/aws/session
go get -v github.com/aws/aws-sdk-go/service/ec2
go get -v github.com/aws/aws-sdk-go/service/s3
go get -v github.com/aws/aws-sdk-go/service/sqs

# Clean and build 
go clean
go build 
go fmt

echo "Build bds go: Finished"

