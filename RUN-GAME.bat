@echo off
title RPG Battle Arena - Play in Console
cd /d "%~dp0"
echo.
echo Starting interactive game...
echo.
call mvn -q compile exec:java -Dexec.mainClass="com.example.rpgbattlearena.ConsoleGame"
echo.
pause
