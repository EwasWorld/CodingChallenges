@echo off

echo.
echo powershell -ExecutionPolicy ByPass -File .\newPuzzleSolver.ps1 %1
powershell -ExecutionPolicy ByPass -File .\newPuzzleSolver.ps1 %1
echo.

if NOT %ERRORLEVEL%==0  (
   exit /b 1
)

"T:\Programs\Microsoft VS Code\bin\code" src/PuzzleSolvers/Puzzle%1.hs
