@echo off
echo ====================================================
echo       🚀 Launching Society OS (Windows)
echo ====================================================

cd /d "%~dp0"

IF EXIST "app\society-os.exe" (
    start "" "app\society-os.exe"
) ELSE (
    java -jar app\society-os.jar
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Failed to launch Society Office OS.
    echo Please ensure Java 17+ is installed or society-os.exe is present in app\ directory.
    pause
)
