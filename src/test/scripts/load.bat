@echo off

call env.bat

type "%MAVEN_BASEDIR%"\target\xq\load.xq | "%JAVA_HOME%"\bin\java -Dexist.home="%EXIST_HOME%" ^^
  -jar "%EXIST_HOME%"\start.jar client -l -u $%EXIST_USERNAME% %EXIST_PASSWORD_ARG% -x
