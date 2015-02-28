#! /bin/bash

source $(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/env.sh

RESULT=$(cat $MAVEN_BASEDIR/target/xq/load.xq | $JAVA_HOME/bin/java -Dexist.home=$EXIST_HOME \
  -Dlog4j.configuration=file:$MAVEN_BASEDIR/src/test/resources/log4j.xml \
  -jar $EXIST_HOME/start.jar client -l -u $EXIST_USERNAME $EXIST_PASSWORD_ARG -x)

# If one of our tasks returns false, fail the build
for LINE in "${RESULT[@]}"; do
  echo "$LINE"

  if [[ "$LINE" == *"Installed new XAR file: false"* ]]; then
    exit 1
  fi
done
