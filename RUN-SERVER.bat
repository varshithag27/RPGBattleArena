@echo off
title RPG Battle Arena - Web Server
cd /d "%~dp0"
echo.
echo ========================================
echo   RPG BATTLE ARENA - Starting Server
echo ========================================
echo.

REM If port 8080 is busy, stop the old server (from a previous run)
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo Port 8080 was busy. Stopping old server ^(PID %%a^)...
    taskkill /PID %%a /F >nul 2>&1
    ping -n 3 127.0.0.1 >nul
    echo.
)

echo Please wait... (first time may take 1-2 minutes)
echo.
echo When you see "Started RpgBattleArenaApplication":
echo   Open your browser and go to:
echo.
echo       http://localhost:8080
echo.
echo Press Ctrl+C in this window to stop the server.
echo.
call mvn spring-boot:run
pause
