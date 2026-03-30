@echo off
setlocal
cd /d "%~dp0"

if "%JAVA_HOME%"=="" (
  echo JAVA_HOME chua duoc set.
  echo Hay set JAVA_HOME toi thu muc JDK 22 (vi du: C:\Program Files\Java\jdk-22)
  pause
  exit /b 1
)

call .\mvnw.cmd clean package
if errorlevel 1 exit /b 1

set "APP_NAME=QuanLyCuaHang"
set "OUT_DIR=dist"

for /f "delims=" %%i in ('dir /b /o:-d "target\*shaded.jar"') do set "JAR_FILE=target\%%i" & goto :found
:found
if "%JAR_FILE%"=="" (
  echo Khong tim thay file jar shade trong target.
  exit /b 1
)

if exist "%OUT_DIR%\%APP_NAME%" rmdir /s /q "%OUT_DIR%\%APP_NAME%"

"%JAVA_HOME%\bin\jpackage.exe" --type app-image --name "%APP_NAME%" --input target --main-jar "%JAR_FILE%" --dest "%OUT_DIR%" --app-version 0.0.1

echo.
echo Da tao app portable tai: %OUT_DIR%\%APP_NAME%
pause

