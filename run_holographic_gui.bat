@echo off
echo ============================================================
echo   Police Response Time Simulator - Holographic Interface
echo ============================================================
echo.
echo Features:
echo - 30 persistent police units across 6 Floyd-Warshall stations
echo - Real pathfinding using road connections
echo - Scrollable large map (2500x1800)
echo - Complete unit lifecycle (dispatch -> mission -> return)
echo - Detailed node information on click
echo.
echo Starting GUI...
echo.
cd src
java -cp . MainGUI
pause
