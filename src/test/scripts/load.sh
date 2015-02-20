#! /bin/bash

source $(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/env.sh

RESULT=$(cat target/xq/load.xq | $JAVA_HOME/bin/java -Dexist.home=$EXIST_HOME -jar $EXIST_HOME/start.jar \
  client -l -u $EXIST_USERNAME $EXIST_PASSWORD_ARG -x)

# If one of our tasks returns false, fail the build
for LINE in "${RESULT[@]}"; do
  echo "$LINE"
  
  if [[ "$RESULT" == *false* ]]; then
    exit 1
  fi
done