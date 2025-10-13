@echo off
REM Script para ejecutar la demostración del simulador en Windows
echo === Simulador de Planificacion - Demostracion ===
echo Compilando y ejecutando la demostracion...
echo.

REM Crear directorio de logs si no existe
if not exist logs mkdir logs

REM Compilar la demostración
echo Compilando demostracion...
javac -d "target/classes" src/main/DemoSimulador.java

if %errorlevel% equ 0 (
    echo Compilacion exitosa
    echo.
    
    REM Ejecutar la demostración
    echo Ejecutando demostracion del simulador...
    echo Presiona Ctrl+C para detener
    echo.
    
    java -cp "target/classes" main.DemoSimulador
) else (
    echo Error en la compilacion
    pause
    exit /b 1
)

pause
