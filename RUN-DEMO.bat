@echo off
title RPG Battle Arena - Quick Demo
cd /d "%~dp0"
echo.
echo Running a short automatic demo...
echo (You will see game output in a few seconds.)
echo.
call mvn -q compile exec:java -Dexec.mainClass="com.example.rpgbattlearena.ConsoleGame" -Dexec.args="demo"
echo.
pause
