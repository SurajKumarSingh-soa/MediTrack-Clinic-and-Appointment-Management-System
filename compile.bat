@echo off
echo ========================================
echo   MediTrack Compilation Script
echo ========================================
echo.

echo Creating bin directory...
if not exist bin mkdir bin

echo Compiling Java files...
javac -d bin -sourcepath src\main\java src\main\java\com\airtribe\meditrack\*.java src\main\java\com\airtribe\meditrack\api\*.java src\main\java\com\airtribe\meditrack\constants\*.java src\main\java\com\airtribe\meditrack\entity\*.java src\main\java\com\airtribe\meditrack\exception\*.java src\main\java\com\airtribe\meditrack\interfaces\*.java src\main\java\com\airtribe\meditrack\service\*.java src\main\java\com\airtribe\meditrack\util\*.java src\main\java\com\airtribe\meditrack\test\*.java

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   Compilation Successful!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo   Compilation Failed!
    echo ========================================
    exit /b 1
)
