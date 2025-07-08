@echo off
title TheKnife Restaurant Management System
echo ====================================================
echo    TheKnife Restaurant Management System
echo ====================================================
echo.

echo Starting TheKnife application...
java -Djava.util.logging.config.file=NUL -jar bin\TheKnife-portable.jar

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to start the application.
    echo Make sure Java is installed.
    pause
)
