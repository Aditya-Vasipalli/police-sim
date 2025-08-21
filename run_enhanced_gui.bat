@echo off
echo Starting Enhanced Police Response Simulator...
cd src
echo Compiling Java files...
javac -cp . gui\HolographicPoliceGUI.java MainGUI.java
if %errorlevel% == 0 (
    echo Compilation successful!
    echo.
    echo ===============================================
    echo SCREEN-FIT POLICE SIMULATOR - READY TO USE!
    echo ===============================================
    echo - PERFECT SCREEN FIT: No scrolling needed (1200x800)
    echo - CLEAN VISUAL INTERFACE: No text labels or clutter
    echo - 6 FLOYD-WARSHALL STATIONS: Correctly pulsing blue stations
    echo - 31 PERSISTENT UNITS: Real pathfinding and unit management
    echo - IMMEDIATELY USABLE: Everything visible at once
    echo - INTERACTIVE ELEMENTS: Click any element for information
    echo ===============================================
    echo.
    echo Starting Ready-to-Use Interface...
    java -cp . MainGUI
) else (
    echo Compilation failed! Check for errors.
    pause
)
