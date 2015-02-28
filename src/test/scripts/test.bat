@echo off

call env.bat

type "%MAVEN_BASEDIR%"\target\xq\test-write-to-file.xq | "%JAVA_HOME%"\bin\java -Dexist.home="%EXIST_HOME%" ^^
  -Dlog4j.configuration=file:"%MAVEN_BASEDIR%"\src\test\resources\log4j.xml ^^
  -jar "%EXIST_HOME%"\start.jar client -l -u %EXIST_USERNAME% %EXIST_PASSWORD_ARG% -x
  
type "%MAVEN_BASEDIR"\target\xq\test-read-from-file.xq | "%JAVA_HOME%"\bin\java -Dexist.home="%EXIST_HOME%" ^^
  -Dlog4j.configuration=file:"%MAVEN_BASEDIR%"\src\test\resources\log4j.xml ^^
  -jar "%EXIST_HOME%"\start.jar client -l -u %EXIST_USERNAME% %EXIST_PASSWORD_ARG% -x