#! /bin/bash

source $(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/env.sh

# Run our WriteToFile tests
RESULTS=$(cat target/xq/test-write-to-file.xq | $JAVA_HOME/bin/java -Dexist.home=$EXIST_HOME \
  -Dlog4j.configuration=file:$MAVEN_BASEDIR/src/test/resources/log4j.xml \
  -jar $EXIST_HOME/start.jar client -l -u $EXIST_USERNAME $EXIST_PASSWORD_ARG -x)
EXIT_CODE=0

for RESULT in "${RESULTS[@]}"
do
  echo "$RESULT"

  if [[ "$RESULT" == *"[ERROR]"* ]] && [ $EXIT_CODE == 0 ]; then
    EXIT_CODE=1
  fi
done

# Run our ReadFromFile tests
RESULTS=$(cat target/xq/test-read-from-file.xq | $JAVA_HOME/bin/java -Dexist.home=$EXIST_HOME \
  -Dlog4j.configuration=file:$MAVEN_BASEDIR/src/test/resources/log4j.xml \
  -jar $EXIST_HOME/start.jar client -l -u $EXIST_USERNAME $EXIST_PASSWORD_ARG -x)

for RESULT in "${RESULTS[@]}"
do
  echo "$RESULT"

  if [[ "$RESULT" == *"[ERROR]"* ]] && [ $EXIT_CODE == 0 ]; then
    EXIT_CODE=1
  fi
done

exit $EXIT_CODE