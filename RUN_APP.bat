@echo off
setlocal
cd /d "%~dp0"

if "%JAVA_HOME%"=="" (
  echo JAVA_HOME chua duoc set.
  echo Hay set JAVA_HOME toi thu muc JDK 22 (vi du: C:\Program Files\Java\jdk-22)
  echo Sau do chay lai file nay.
  pause
  exit /b 1
)

call .\mvnw.cmd -Dexec.mainClass=com.cuahang.main.MainApp exec:java

pause

