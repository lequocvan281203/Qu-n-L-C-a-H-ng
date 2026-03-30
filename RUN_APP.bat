@echo off
setlocal
cd /d "%~dp0"

if "%JAVA_HOME%"=="" (
  for /f "delims=" %%i in ('where java.exe 2^>nul') do set "JAVAPATH=%%i" & goto :foundJava
  echo JAVA_HOME chua duoc set va khong tim thay java.exe trong PATH.
  echo Hay set JAVA_HOME toi thu muc JDK 22 (vi du: C:\Program Files\Java\jdk-22)
  echo Sau do chay lai file nay.
  pause
  exit /b 1
)
:foundJava
if not "%JAVAPATH%"=="" (
  for %%d in ("%JAVAPATH%\..") do set "BIN_DIR=%%~fd"
  for %%d in ("%BIN_DIR%\..") do set "JAVA_HOME=%%~fd"
)

call .\mvnw.cmd -Dexec.mainClass=com.cuahang.main.MainApp exec:java

pause
