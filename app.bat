@if "%DEBUG%"=="" @echo off
:: Run one the application.
:: The first argument must be the command name (e.g. resolution).
:: Any remaining arguments are forwarded to the sample's argv.

if "%OS%"=="Windows_NT" setlocal EnableDelayedExpansion

set ARGS=%*
set ARGS=!ARGS:*%1=!
if "!ARGS:~0,1!"==" " set ARGS=!ARGS:~1!

if exist "build\install\" (
  call "build\install\exercise4_project\bin\exercise4_project" %ARGS%
) else (
  call gradlew --quiet ":installDist" && call "build\install\exercise4_project\bin\exercise4_project" %ARGS%
)
if "%OS%"=="Windows_NT" endlocal