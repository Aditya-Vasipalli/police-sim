@echo off
echo === Police Response Time Simulation - Build and Run ===
echo.

echo Compiling all Java files...
javac -cp src src/models/*.java src/algorithms/*.java src/*.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

echo Running PathfindingDemo...
echo.
java -cp src PathfindingDemo

echo.
echo.
echo Running ComponentTest...
echo.
java -cp src ComponentTest

echo.
echo === All tests completed ===
pause
