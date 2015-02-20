#! /bin/bash

if [ -z "$EXIST_HOME" ]; then
  echo "[ERROR] The EXIST_HOME is not set"
  exit 1
elif [ ! -d "$EXIST_HOME" ]; then
  echo "[ERROR] The EXIST_HOME \"$EXIST_HOME\" does not resolve to an exist-db directory"
  exit 1
fi

if [ -z "$EXIST_USERNAME" ]; then
  echo "[ERROR] The EXIST_USERNAME is not set"
  exit 1
fi

if [ -z "$JAVA_HOME" ]; then
  echo "[ERROR] The JAVA_HOME is not set"
  exit 1
fi

# Check if our admin user has a password set
if [ "$EXIST_PASSWORD"  != 'EMPTY' ]; then
  EXIST_PASSWORD_ARG="-P $EXIST_PASSWORD"
fi