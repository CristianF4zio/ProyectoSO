@echo off
REM Script para compilar y ejecutar el simulador de planificación en Windows
REM Autor: Simulador de Planificación

echo === Simulador de Planificacion ===
echo Compilando y ejecutando el simulador...
echo.

REM Crear directorio de logs si no existe
if not exist logs mkdir logs

REM Compilar el proyecto
echo Compilando proyecto...
javac -cp "src/main" -d "target/classes" src/main\**\*.java src/main\simuladorPlanificacion\**\*.java

if %errorlevel% equ 0 (
    echo Compilacion exitosa
    echo.
    
    REM Ejecutar el simulador
    echo Ejecutando simulador...
    echo Presiona Ctrl+C para detener
    echo.
    
    java -cp "target/classes" main.SimuladorMain
) else (
    echo Error en la compilacion
    pause
    exit /b 1
)

pause
