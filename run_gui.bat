@echo off
echo === Police Response Time Simulation - GUI Launcher ===
echo.

echo Compiling GUI components...
javac -cp src src/PoliceAssignmentPanel.java src/CrimeFeedPanel.java src/PathfindingPanel.java src/PoliceSimulatorGUI.java src/GUIDemo.java

if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

:menu
echo Select an option:
echo 1. Run Complete GUI Application (PoliceSimulatorGUI)
echo 2. Run GUI Component Demos (GUIDemo)
echo 3. Run PathfindingDemo (Console)
echo 4. Exit
echo.
set /p choice="Enter your choice (1-4): "

if "%choice%"=="1" (
    echo.
    echo Launching Police Simulator GUI...
    java -cp src PoliceSimulatorGUI
    goto menu
)

if "%choice%"=="2" (
    echo.
    echo Launching GUI Component Demos...
    java -cp src GUIDemo
    goto menu
)

if "%choice%"=="3" (
    echo.
    echo Running PathfindingDemo...
    java -cp src PathfindingDemo
    pause
    goto menu
)

if "%choice%"=="4" (
    echo Goodbye!
    exit /b 0
)

echo Invalid choice. Please try again.
goto menu
