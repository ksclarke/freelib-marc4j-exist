#! /bin/bash

source $(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/env.sh

# Run our WriteToFile tests
RESULTS=$(cat "$MAVEN_BASEDIR"/target/xq/test-write-to-file.xq | "$JAVA_HOME"/bin/java -Dexist.home="$EXIST_HOME" \
  -Dlog4j.configuration=file:"$MAVEN_BASEDIR"/src/test/resources/log4j.xml -cp $EXIST_HOME/lib/core/*.jar \
  -jar "$EXIST_HOME"/start.jar client -l -u $EXIST_USERNAME $EXIST_PASSWORD_ARG -x 2>&1)
EXIT_CODE=0

for LINE in "${RESULTS[@]}"
do
  # It's okay that we cannot load every jar in the lib/core directory
  if [[ $LINE != *"Could not find or load main class"* ]]; then
    echo "$LINE"
  fi

  if [[ "$LINE" == *"[ERROR]"* ]] && [ $EXIT_CODE == 0 ]; then
    EXIT_CODE=1
  fi
done

# Run our ReadFromFile tests
RESULTS=$(cat "$MAVEN_BASEDIR"/target/xq/test-read-from-file.xq | "$JAVA_HOME"/bin/java -Dexist.home="$EXIST_HOME" \
  -Dlog4j.configuration=file:"$MAVEN_BASEDIR"/src/test/resources/log4j.xml -cp $EXIST_HOME/lib/core/*.jar \
  -jar "$EXIST_HOME"/start.jar client -l -u $EXIST_USERNAME $EXIST_PASSWORD_ARG -x 2>&1)

for LINE in "${RESULTS[@]}"
do
  # It's okay that we cannot load every jar in the lib/core directory
  if [[ $LINE != *"Could not find or load main class"* ]]; then
    echo "$LINE"
  fi

  if [[ "$LINE" == *"[ERROR]"* ]] && [ $EXIT_CODE == 0 ]; then
    EXIT_CODE=1
  fi
done

exit $EXIT_CODE