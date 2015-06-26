#! /bin/bash

source $(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/env.sh

RESULT=$(cat "$MAVEN_BASEDIR"/target/xq/load.xq | "$JAVA_HOME"/bin/java -Dexist.home="$EXIST_HOME" \
  -Dlog4j.configuration=file:"$MAVEN_BASEDIR"/src/test/resources/log4j.xml -cp $EXIST_HOME/lib/core/*.jar \
  -jar "$EXIST_HOME"/start.jar client -l -u $EXIST_USERNAME $EXIST_PASSWORD_ARG -x 2>&1)

# If one of our tasks returns false, fail the build
for LINE in "${RESULT[@]}"; do
  # It's okay that we cannot load every jar in the lib/core directory
  if [[ $LINE != *"Could not find or load main class"* ]]; then
    echo "$LINE"
  fi

  if [[ "$LINE" == *"Installed new XAR file: false"* ]]; then
    exit 1
  fi
done
