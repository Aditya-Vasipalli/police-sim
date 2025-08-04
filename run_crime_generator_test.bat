@echo off
echo Compiling Crime Generator Module...
echo.

REM Create a temporary directory for compiled classes
if not exist "temp" mkdir temp

REM Compile all necessary Java files
echo Compiling Crime model...
javac -d temp src\models\Crime.java

echo Compiling CrimeDAO...
javac -cp temp -d temp src\database\CrimeDAO.java

echo Compiling CityMap (stub)...
javac -cp temp -d temp src\CityMap.java

echo Compiling CrimeGenerator...
javac -cp temp -d temp src\CrimeGenerator.java

echo Compiling Test class...
javac -cp temp -d temp src\CrimeGeneratorTest.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilation successful!
    echo.
    echo Running CrimeGenerator test...
    echo ================================
    java -cp temp CrimeGeneratorTest
    echo ================================
    echo.
    echo ✅ CrimeGenerator module is ready for submission!
) else (
    echo.
    echo ❌ Compilation failed. Please check the errors above.
)

echo.
echo Cleaning up temporary files...
rmdir /s /q temp

pause
