@echo off
setlocal
cd /d "%~dp0"

if "%JAVA_HOME%"=="" (
  for /f "delims=" %%i in ('where jpackage.exe 2^>nul') do set "JPKG=%%i" & goto :foundJpkg
  echo JAVA_HOME chua duoc set va khong tim thay jpackage.exe trong PATH.
  echo Hay set JAVA_HOME toi thu muc JDK 22 (vi du: C:\Program Files\Java\jdk-22)
  pause
  exit /b 1
)
:foundJpkg
if not "%JPKG%"=="" (
  for %%d in ("%JPKG%\..") do set "BIN_DIR=%%~fd"
  for %%d in ("%BIN_DIR%\..") do set "JAVA_HOME=%%~fd"
)

call .\mvnw.cmd clean package
if errorlevel 1 exit /b 1

set "APP_NAME=QuanLyCuaHang"
set "OUT_DIR=dist-installer"

for /f "delims=" %%i in ('dir /b /o:-d "target\*shaded.jar" 2^>nul') do set "JAR_BASENAME=%%i" & goto :found
:found
if "%JAR_BASENAME%"=="" (
  echo Khong tim thay file jar shade trong target.
  exit /b 1
)

if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"

"%JAVA_HOME%\bin\jpackage.exe" --type exe --name "%APP_NAME%" --input target --main-jar "%JAR_BASENAME%" --main-class com.cuahang.main.MainApp --dest "%OUT_DIR%" --app-version 0.0.1

echo.
echo Da tao installer EXE tai: %OUT_DIR%
pause
