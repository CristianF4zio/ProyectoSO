@echo off
echo Compilando Simulador de Sistema Operativo...
mvn clean compile
if %ERRORLEVEL% EQU 0 (
    echo Compilacion exitosa!
) else (
    echo Error en la compilacion!
)
pause
