#!/bin/bash

VERSION=$1

npm version $VERSION
cd client
npm version $VERSION
cd ..
cd server
npm version $VERSION
cd ..
git add .
git commit -m "Release v$VERSION"
git push
git tag extension-vscode-v$VERSION
git push --tags